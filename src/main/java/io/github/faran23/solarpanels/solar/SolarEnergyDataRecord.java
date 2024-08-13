package io.github.faran23.solarpanels.solar;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SolarEnergyDataRecord(int energy, int gen, int transfer, int capacity) {

    public static final Codec<SolarEnergyDataRecord> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.INT.fieldOf("energy").forGetter(SolarEnergyDataRecord::energy),
                    Codec.INT.fieldOf("gen").forGetter(SolarEnergyDataRecord::gen),
                    Codec.INT.fieldOf("transfer").forGetter(SolarEnergyDataRecord::transfer),
                    Codec.INT.fieldOf("capacity").forGetter(SolarEnergyDataRecord::capacity)
            ).apply(builder, SolarEnergyDataRecord::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SolarEnergyDataRecord> NETWORK_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SolarEnergyDataRecord::energy,
            ByteBufCodecs.VAR_INT,
            SolarEnergyDataRecord::gen,
            ByteBufCodecs.VAR_INT,
            SolarEnergyDataRecord::transfer,
            ByteBufCodecs.VAR_INT,
            SolarEnergyDataRecord::capacity,
            SolarEnergyDataRecord::new
    );
}
