package io.github.faran23.solarpanels.solar;

import net.minecraftforge.energy.IEnergyStorage;

public class SolarEnergy implements IEnergyStorage {

    private final IEnergyStorage energy;

    public SolarEnergy(IEnergyStorage energy) {
        this.energy = energy;
    }


    @Override
    public int receiveEnergy(int i, boolean b) {
        return energy.receiveEnergy(i, b);
    }

    @Override
    public int extractEnergy(int i, boolean b) {
        return energy.extractEnergy(i, b);
    }

    @Override
    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return energy.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return energy.canExtract();
    }

    @Override
    public boolean canReceive() {
        return energy.canReceive();
    }
}
