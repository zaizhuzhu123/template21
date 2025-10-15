//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.template21.frame.p6spy;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author qmf
 */
public class CustomP6spySqlFormat implements MessageFormattingStrategy {

    private static final ThreadLocal<String> TX_ID_HOLDER = new ThreadLocal<>();

    public CustomP6spySqlFormat() {
    }

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        boolean inTx = TransactionSynchronizationManager.isActualTransactionActive();
        String txId = TX_ID_HOLDER.get();
        if (inTx && txId == null) {
            // 第一次进入事务，生成唯一 ID
            txId = "TX-" + System.nanoTime();
            TX_ID_HOLDER.set(txId);
            TransactionSynchronizationManager.registerSynchronization(new org.springframework.transaction.support.TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    TX_ID_HOLDER.remove();
                }
            });
        }

        if (!inTx) {
            txId = "0";
        }


        return elapsed + "ms|txId " + txId + "|" + P6Util.singleLine(sql);
    }
}
