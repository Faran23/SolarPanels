package io.github.faran23.solarpanels.solar;

import io.github.faran23.solarpanels.Config;
import io.github.faran23.solarpanels.GroupColor;
import io.github.faran23.solarpanels.register.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SolarPanelBlockEntity extends BlockEntity {

    private EnergyStorage energy;
    private Lazy<IEnergyStorage> energyHandler;

    private int energyGen;
    private int maxEnergyTransfer;
    private int maxEnergyStored;

    private int currentEnergyTransfer;
    private int currentEnergyGen;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.SOLAR_BE.get(), pos, state);
        setEnergyValues();

        currentEnergyGen = 0;
        currentEnergyTransfer = 0;
    }

    private void setupEnergy() {
        this.energy = createEnergyStorage();
        this.energyHandler = Lazy.of(() -> energy);
    }

    private void setEnergyValues() {
        this.energyGen = Config.initialGenerationRate;
        this.maxEnergyTransfer = Config.initialTransferRate;
        this.maxEnergyStored = Config.initialMaxEnergy;
        setupEnergy();
    }

    public void upgrade(Config.Tier tier, ServerLevel level) {
        this.energyGen += tier.genIncrease;
        this.maxEnergyTransfer += tier.transferIncrease;
        this.maxEnergyStored += tier.capacityIncrease;

        upgradeEffects(level);

        int oldEnergy = energy.getEnergyStored();
        setupEnergy();
        while (oldEnergy > 0) {
            oldEnergy -= energy.receiveEnergy(oldEnergy, false);
        }
    }

    private void upgradeEffects(ServerLevel level) {
        level.sendParticles(ParticleTypes.SCRAPE, worldPosition.getX() + 0.5, worldPosition.getY() + 0.25, worldPosition.getZ() + 0.5,
                8, 0.5, 0, 0.5, 0.1);
        level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.8f, 1.0f);
    }


    public void tickServer() {
        if (level == null) return;

        boolean powered = level.canSeeSky(worldPosition) && level.isDay() || Config.shouldAlwayGenerate;

        if (powered) {
            currentEnergyGen = generateEnergy();
        }

        if (powered != getBlockState().getValue(BlockStateProperties.POWERED)) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, powered));
        }

        currentEnergyTransfer = sendEnergy();
    }

    private int generateEnergy() {
        if (energy.getEnergyStored() < energy.getMaxEnergyStored()) {
            int generating = energy.receiveEnergy(energyGen, false);
            setChanged();
            return generating;
        }
        return 0;
    }

    private int sendEnergy() {
        // send down only
        if (level == null || energy.getEnergyStored() <= 0) return 0;
        IEnergyStorage energyBelow = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.below(), Direction.UP);
        if (energyBelow != null) {
            if (energyBelow.canReceive()) {
                int received = energyBelow.receiveEnergy(Math.min(energy.getEnergyStored(), maxEnergyTransfer), false);
                energy.extractEnergy(received, false);
                setChanged();
                return received;
            }
        }
        return 0;
    }

    public int getStoredEnergy() {
        return energy.getEnergyStored();
    }

    public int getMaxEnergy() {
        return maxEnergyStored;
    }

    public int getMaxEnergyTransfer() {
        return maxEnergyTransfer;
    }

    public int getCurrentEnergyTransfer() {
        return currentEnergyTransfer;
    }

    public int getMaxEnergyGen() {
        return energyGen;
    }

    public int getCurrentEnergyGen() {
        return currentEnergyGen;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energy.serializeNBT());
        tag.putInt("Gen", energyGen);
        tag.putInt("Transfer", maxEnergyTransfer);
        tag.putInt("Capacity", maxEnergyStored);
        tag.putString("Color", getBlockState().getValue(SolarPanelBlock.COLOR).getSerializedName());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Gen") && tag.contains("Transfer") && tag.contains("Capacity")) {
            energyGen = tag.getInt("Gen");
            maxEnergyTransfer = tag.getInt("Transfer");
            maxEnergyStored = tag.getInt("Capacity");
            setupEnergy();
        }
        if (tag.contains("Energy")) {
            energy.deserializeNBT(tag.get("Energy"));
        }
        if (tag.contains("Color") && level != null) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SolarPanelBlock.COLOR, GroupColor.fromString(tag.getString("Color"))));
        }
    }

    @Nonnull
    private EnergyStorage createEnergyStorage() {
        return new EnergyStorage(maxEnergyStored, maxEnergyTransfer, maxEnergyTransfer);
    }

    public IEnergyStorage getEnergyHandler() {
        return energyHandler.get();
    }

}
