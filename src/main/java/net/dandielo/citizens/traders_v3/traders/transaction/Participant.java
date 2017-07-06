package net.dandielo.citizens.traders_v3.traders.transaction;

import java.util.UUID;

public interface Participant {

   boolean isPlayer();

   UUID getUUID();

   boolean check(double var1);

   boolean withdraw(double var1);

   boolean deposit(double var1);
}
