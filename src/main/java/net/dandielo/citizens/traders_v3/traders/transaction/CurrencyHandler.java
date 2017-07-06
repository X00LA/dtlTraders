package net.dandielo.citizens.traders_v3.traders.transaction;

import java.util.List;
import net.dandielo.citizens.traders_v3.traders.transaction.TransactionInfo;

public interface CurrencyHandler {

   boolean finalizeTransaction(TransactionInfo var1);

   boolean allowTransaction(TransactionInfo var1);

   void getDescription(TransactionInfo var1, List var2);

   double getTotalPrice(TransactionInfo var1);

   String getName();
}
