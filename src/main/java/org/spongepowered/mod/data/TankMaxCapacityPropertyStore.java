/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.mod.data;

import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.extra.fluid.data.property.TankMaxCapacityProperty;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.common.data.property.store.common.AbstractSpongePropertyStore;

import java.util.Optional;

public class TankMaxCapacityPropertyStore extends AbstractSpongePropertyStore<TankMaxCapacityProperty> {

    @Override
    public Optional<TankMaxCapacityProperty> getFor(Location<World> location) {
        final Optional<TileEntity> tileEntity = location.getTileEntity();
        if (tileEntity.isPresent()) {
            final IFluidHandler tank = ((net.minecraft.tileentity.TileEntity) tileEntity.get())
                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (tank != null) {
                int capacity = 0;
                for (IFluidTankProperties p : tank.getTankProperties()) {
                    capacity += p.getCapacity();
                }
                return Optional.of(new TankMaxCapacityProperty(capacity));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<TankMaxCapacityProperty> getFor(Location<World> location, Direction direction) {
        return Optional.empty();
    }

}
