package kieker.tpmon.core;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import kieker.tpmon.annotation.TpmonInternal;
import kieker.tpmon.monitoringRecord.executions.RemoteCallMetaData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * ==================LICENCE=========================
 * Copyright 2006-2009 Kieker Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================================
 */

/**
 *
 * @author Andre van Hoorn
 */
public class ControlFlowRegistry {

    private static final Log log = LogFactory.getLog(ControlFlowRegistry.class);
    private static final ControlFlowRegistry instance = new ControlFlowRegistry();
    private final AtomicLong lastThreadId;
    private final ThreadLocal<Long> threadLocalTraceId = new ThreadLocal<Long>();
    private final ThreadLocal<Integer> threadLocalEoi = new ThreadLocal<Integer>();
    private final ThreadLocal<Integer> threadLocalEss = new ThreadLocal<Integer>();

    private ControlFlowRegistry() {
        /* In order to (probabilistically!) avoid that other instances in our
         * system (on another node, in another vm, ...) generate the same thread
         * ids,  we fill the left-most 16 bits of the thread id with a uniquely
         * distributed random number (0,0000152587890625 = 0,00152587890625 %).
         * As a consequence, this constitutes a uniquely distributed offset of
         * size 2^(64-1-16) = 2^47 = 140737488355328L in the worst case.
         * Note that we restrict ourselves to the positive long values so far.
         * Of course, negative values may occur (as a result of an overflow) --
         * this does not hurt!
         */
        Random r = new Random();
        long base = ((long)r.nextInt(65536) << (Long.SIZE-16-1));
        /* can be removed if considered stable
        log.info("base 0:" + ((long)0 << (Long.SIZE-16-1)));
        log.info("base 65535:" + ((long)65535 << (Long.SIZE-16-1)));
        log.info("base 65534:" + ((long)65534 << (Long.SIZE-16-1)));
        log.info("base 65534+1:" + ((long)65534+1 << (Long.SIZE-16-1)));
        log.info("base 65534+2:" + ((long)65534+2 << (Long.SIZE-16-1)));
        log.info("base r:" + ((long)r.nextInt(65536) << (Long.SIZE-16-1)));
        log.info("overflow?: " + (((long)65535 << (Long.SIZE-16-1))+ 140737488355328L));
        log.info("Long.SIZE: " + Long.SIZE);
        log.info("2^47="+Math.pow(2, 47));
        log.info("Long.MAX_VALUE: " + Long.MAX_VALUE);
        log.info("Long.MIN_VALUE: " + Long.MIN_VALUE);
        log.info("Long.MAX_VALUE+1: " + (Long.MAX_VALUE+1));
        */
        //long idBase = (long)r.nextInt(1024) << (Long.SIZE-10+1); // 1024 = 2^10-1
        lastThreadId = new AtomicLong(base);
        log.info("First threadId will be " + (base + 1));
    }

    @TpmonInternal()
    public static final ControlFlowRegistry getInstance() {
        return ControlFlowRegistry.instance;
    }

    /**
     * This methods returns a globally unique trace id.
     *
     * @return
     */
    @TpmonInternal()
    public final long getUniqueTraceId(){
        return lastThreadId.incrementAndGet();
    }

    /**
     * This method returns a thread-local traceid which is globally
     * unique and stored it local for the thread.
     * The thread is responsible for invalidating the stored curTraceId using
     * the method unsetThreadLocalTraceId()!
     */
    @TpmonInternal()
    public final long getAndStoreUniqueThreadLocalTraceId() {
        long id = lastThreadId.incrementAndGet();
        this.threadLocalTraceId.set(id);
        return id;
    }

    /**
     * This method stores a thread-local curTraceId.
     * The thread is responsible for invalidating the stored curTraceId using
     * the method unsetThreadLocalTraceId()!
     */
    @TpmonInternal()
    public final void storeThreadLocalTraceId(long traceId) {
        this.threadLocalTraceId.set(traceId);
    }

    /**
     * This method returns the thread-local traceid previously
     * registered using the method registerTraceId(curTraceId).
     *
     * @return the traceid. -1 if no curTraceId has been registered
     *         for this thread.
     */
    @TpmonInternal()
    public final long recallThreadLocalTraceId() {
        //log.info("Recalling curTraceId");
        Long traceIdObj = this.threadLocalTraceId.get();
        if (traceIdObj == null) {
            //log.info("curTraceId == null");
            return -1;
        }
//log.info("curTraceId =" + traceIdObj);
        return traceIdObj;
    }

    /**
     * This method unsets a previously registered traceid.
     */
    @TpmonInternal()
    public final void unsetThreadLocalTraceId() {
        this.threadLocalTraceId.remove();
    }

    /**
     * Used by the spring aspect to explicitly register an curEoi.
     * The thread is responsible for invalidating the stored curTraceId using
     * the method unsetThreadLocalEOI()!
     */
    @TpmonInternal()
    public final void storeThreadLocalEOI(int eoi) {
        //log.info(Thread.currentThread().getId());
        this.threadLocalEoi.set(eoi);
    }

    /**
     * Since this method accesses a ThreadLocal variable,
     * it is not (necessary to be) thread-safe.
     */
    @TpmonInternal()
    public final int incrementAndRecallThreadLocalEOI() {
        //log.info(Thread.currentThread().getId());
        Integer curEoi = this.threadLocalEoi.get();
        if (curEoi == null) {
            log.fatal("eoi has not been registered before");
            return -1;
        }
        int newEoi = curEoi + 1;
        this.threadLocalEoi.set(newEoi);
        return newEoi;
    }

    /**
     * This method returns the thread-local curEoi previously
     * registered using the method registerTraceId(curTraceId).
     *
     * @return the sessionid. -1 if no curEoi registered.
     */
    @TpmonInternal()
    public final int recallThreadLocalEOI() {
        Integer curEoi = this.threadLocalEoi.get();
        if (curEoi == null) {
            log.fatal("eoi has not been registered before");
            return -1;
        }
        return curEoi;
    }

    /**
     * This method unsets a previously registered traceid.
     */
    @TpmonInternal()
    public final void unsetThreadLocalEOI() {
        this.threadLocalEoi.remove();
    }

    /**
     * Used by the spring aspect to explicitly register a sessionid that is to be collected within
     * a servlet method (that knows the request object).
     * The thread is responsible for invalidating the stored curTraceId using
     * the method unsetThreadLocalSessionId()!
     */
    @TpmonInternal()
    public final void storeThreadLocalESS(int ess) {
        this.threadLocalEss.set(ess);
    }

    /**
     * Since this method accesses a ThreadLocal variable,
     *  it is not (necessary to be) thread-safe.
     */
    @TpmonInternal()
    public final int recallAndIncrementThreadLocalESS() {
        Integer curEss = this.threadLocalEss.get();
        if (curEss == null) {
            log.fatal("ess has not been registered before");
            return -1;
        }
        this.threadLocalEss.set(curEss + 1);
        return curEss;
    }

    /**
     * This method returns the thread-local curEss previously
     * registered using the method registerTraceId(curTraceId).
     *
     * @return the sessionid. -1 if no curEss registered.
     */
    @TpmonInternal()
    public final int recallThreadLocalESS() {
        Integer ess = this.threadLocalEss.get();
        if (ess == null) {
            log.fatal("ess has not been registered before");
            return -1;
        }
        return ess;
    }

    /**
     * This method unsets a previously registered curEss.
     */
    @TpmonInternal()
    public final void unsetThreadLocalESS() {
        this.threadLocalEss.remove();
    }

    /**
     * Use a RemoteCallMetaData object to transport tracing data together
     * with a remote method call. This is required to allows  tracing in
     * distributed systems.
     *
     * @param threadid
     */
    @TpmonInternal()
    public final RemoteCallMetaData getRemoteCallMetaData() {
        long curTraceId = this.recallThreadLocalTraceId();
        if (curTraceId == -1) { // no curTraceId was registered
            log.info("Tpmon: warning traceid was null");
            curTraceId = this.getAndStoreUniqueThreadLocalTraceId();
        }

        int curEoi = this.recallThreadLocalEOI();
        if (curEoi == -1) {
            log.info("Tpmon: warning eoi == -1");
            curEoi = 0;
            this.storeThreadLocalEOI(0);
        }

        int curEss = this.recallThreadLocalESS();
        if (curEss == -1) {
            log.info("Tpmon: warning ess == -1");
            curEss = 0;
            this.storeThreadLocalESS(0);
        }

        return new RemoteCallMetaData(curTraceId, curEoi, curEss);
    }

    /**
     * This method has to be called to register an incomming remote call
     * @param rcmd
     * @param threadid
     */
    @TpmonInternal()
    public final void registerRemoteCallMetaData(RemoteCallMetaData rcmd) {
        if (rcmd == null) {
            log.info("Tpmon: RCMD == null");
            this.getAndStoreUniqueThreadLocalTraceId();
            this.storeThreadLocalEOI(0);
            this.storeThreadLocalESS(0);
        } else {
            this.storeThreadLocalTraceId(rcmd.traceid);
            this.storeThreadLocalEOI(rcmd.eoi);
            this.storeThreadLocalESS(rcmd.ess);
        }
    }
}
