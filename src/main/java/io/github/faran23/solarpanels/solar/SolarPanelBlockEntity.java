package io.github.faran23.solarpanels.solar;

import io.github.faran23.solarpanels.Config;
import io.github.faran23.solarpanels.GroupColor;
import io.github.faran23.solarpanels.register.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SolarPanelBlockEntity extends BlockEntity {

    private EnergyStorage energy;
    private LazyOptional<IEnergyStorage> energyHandler;

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
        this.energyHandler = LazyOptional.of(() -> new SolarEnergy(energy) {

            @Override
            public int receiveEnergy(int i, boolean b) {
                return 0;
            }

            @Override
            public int extractEnergy(int i, boolean b) {
                return 0;
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return false;
            }
        });
    }

    private void setEnergyValues() {
        this.energyGen = Config.initialGenerationRate;
        this.maxEnergyTransfer = Config.initialTransferRate;
        this.maxEnergyStored = Config.initialMaxEnergy;
        setupEnergy();
    }

    public void upgrade(Config.Tier tier, ServerLevel level) {
        long newGen = energyGen + tier.genIncrease;
        long newTransfer = maxEnergyTransfer + tier.transferIncrease;
        long newCapacity = maxEnergyStored + tier.capacityIncrease;

        // config values will never be > MAX_INT, so this effectively checks for overflow too
        this.energyGen = (int) Math.min(newGen, Config.maxGenerationRate);
        this.maxEnergyTransfer = (int) Math.min(newTransfer, Config.maxTransferRate);
        this.maxEnergyStored = (int) Math.min(newCapacity, Config.maxCapacity);

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
        BlockEntity be = level.getBlockEntity(getBlockPos().below());
        if (be != null) {
            return be.getCapability(ForgeCapabilities.ENERGY).map(e -> {
                if (e.canReceive()) {
                    int received = e.receiveEnergy(Math.min(energy.getEnergyStored(), maxEnergyTransfer), false);
                    energy.extractEnergy(received, false);
                    setChanged();
                    return received;
                }
                return 0;
            }).orElse(0);
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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap);
    }
}
