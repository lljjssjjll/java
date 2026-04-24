package collection;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Threads;

public class Practice_ConcurrentHashMap {
    private static final int THREADS = 8;
    private static final int OPS_PER_THREAD = 50_000;

    public static void main(String[] args) throws Exception {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 멀티스레드 환경에서 안전한 키-값 저장 / 높은 동시성 처리
    static void demonstratePurpose() throws InterruptedException {
        System.out.println("=== [용도] ConcurrentHashMap ===");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // 여러 스레드가 동시에 put/get 해도 안전
        Thread[] threads = new Thread[4];
        for (int t = 0; t < threads.length; t++) {
            final int id = t;
            threads[t] = new Thread(() -> {
                map.put("key-" + id, id * 10);
                map.put("shared", id); // 동시 쓰기 — 손상 없음
            });
            threads[t].start();
        }
        for (Thread th : threads) th.join();
        System.out.println("4개 스레드 동시 put 후 크기 : " + map.size() + " (데이터 손상 없음)");

        // 원자적 복합 연산
        map.put("counter", 0);
        map.compute("counter", (k, v) -> v == null ? 1 : v + 1); // 원자적 증가
        System.out.println("compute() 원자적 증가 후     : " + map.get("counter"));

        map.putIfAbsent("newKey", 42);
        map.putIfAbsent("newKey", 99); // 이미 존재 → 무시
        System.out.println("putIfAbsent 결과             : " + map.get("newKey") + "  (첫 번째 값 유지)");
        System.out.println();
    }

    // 장점: 버킷 단위 잠금(Java 8+ CAS) — 전체 잠금 없이 높은 동시성
    static void demonstrateAdvantages() throws InterruptedException {
        System.out.println("=== [장점] ConcurrentHashMap ===");

        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(THREADS);
        Thread[] threads = new Thread[THREADS];

        long start = System.nanoTime();
        for (int t = 0; t < THREADS; t++) {
            final int base = t * OPS_PER_THREAD;
            threads[t] = new Thread(() -> {
                for (int i = 0; i < OPS_PER_THREAD; i++) map.put(base + i, i);
                latch.countDown();
            });
            threads[t].start();
        }
        latch.await();
        long elapsed = System.nanoTime() - start;

        System.out.println(THREADS + "개 스레드 동시 put " + (THREADS * OPS_PER_THREAD) + "회 : " + elapsed / 1_000_000 + " ms");
        System.out.println("→ 버킷 단위 CAS/synchronized — 서로 다른 버킷은 동시 접근 허용");
        System.out.println();
    }

    // 단점: 단일 스레드에서 HashMap보다 느림(CAS·volatile 오버헤드) / size()는 추정값
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] ConcurrentHashMap ===");

        ConcurrentHashMap<Integer, Integer> chm = new ConcurrentHashMap<>();
        HashMap<Integer, Integer> hm = new HashMap<>(OPS_PER_THREAD * 2);
        int n = OPS_PER_THREAD;

        long start = System.nanoTime();
        for (int i = 0; i < n; i++) chm.put(i, i);
        long chmTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < n; i++) hm.put(i, i);
        long hmTime = System.nanoTime() - start;

        System.out.printf("단일 스레드 put %,d회 → ConcurrentHashMap: %,d ns  HashMap: %,d ns%n", n, chmTime, hmTime);
        System.out.println("→ CAS·volatile 접근 비용으로 단일 스레드에선 HashMap이 빠름");

        // size()는 LongAdder 합산 추정값 — 동시 수정 중엔 정확하지 않을 수 있음
        System.out.println("size() 반환값 : " + chm.size() + "  (단일 스레드라 정확, 동시 수정 중엔 추정값)");

        // null 키/값 불가
        try {
            chm.put(null, 1);
        } catch (NullPointerException e) {
            System.out.println("null 키 삽입  : NullPointerException");
        }
        System.out.println();
    }

    // 성능: HashMap(단일) vs ConcurrentHashMap(단일) / SynchronizedMap(멀티 8개 스레드) vs ConcurrentHashMap(멀티 8개 스레드)
    static void demonstratePerformance() throws Exception {
        System.out.println("=== [성능] ConcurrentHashMap vs synchronizedMap (멀티스레드) ===");
        Options opt = new OptionsBuilder()
                .include("Practice_ConcurrentHashMap\\..*Benchmark")
                .warmupIterations(2)
                .measurementIterations(2)
                .warmupTime(TimeValue.seconds(1))
                .measurementTime(TimeValue.seconds(1))
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @State(Scope.Benchmark)
    public static class ConcurrentBenchmark {
        ConcurrentHashMap<Integer, Integer> chm;
        Map<Integer, Integer> syncMap;

        @Setup(Level.Trial)
        public void setup() {
            chm = new ConcurrentHashMap<>();
            syncMap = Collections.synchronizedMap(new HashMap<>());
            for (int i = 0; i < 10_000; i++) {
                chm.put(i, i);
                syncMap.put(i, i);
            }
        }

        @Benchmark
        @Threads(8)
        public void concurrentHashMap_concurrent_put() {
            chm.put(ThreadLocalRandom.current().nextInt(10_000), 1);
        }

        @Benchmark
        @Threads(8)
        public void synchronizedMap_concurrent_put() {
            syncMap.put(ThreadLocalRandom.current().nextInt(10_000), 1);
        }

        @Benchmark
        @Threads(8)
        public Integer concurrentHashMap_concurrent_get() {
            return chm.get(ThreadLocalRandom.current().nextInt(10_000));
        }

        @Benchmark
        @Threads(8)
        public Integer synchronizedMap_concurrent_get() {
            return syncMap.get(ThreadLocalRandom.current().nextInt(10_000));
        }
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @State(Scope.Thread)
    public static class SingleThreadBenchmark {
        ConcurrentHashMap<Integer, Integer> chm;
        HashMap<Integer, Integer> hm;
        int idx;

        @Setup(Level.Trial)
        public void setup() {
            chm = new ConcurrentHashMap<>();
            hm = new HashMap<>();
            for (int i = 0; i < OPS_PER_THREAD; i++) {
                chm.put(i, i);
                hm.put(i, i);
            }
        }

        @Setup(Level.Iteration)
        public void resetIdx() { idx = 0; }

        @Benchmark
        public Integer concurrentHashMap_get() {
            return chm.get(idx++ % OPS_PER_THREAD);
        }

        @Benchmark
        public Integer hashMap_get() {
            return hm.get(idx++ % OPS_PER_THREAD);
        }

        @Benchmark
        public Integer concurrentHashMap_put() {
            int i = idx++ % OPS_PER_THREAD;
            return chm.put(i, i);
        }

        @Benchmark
        public Integer hashMap_put() {
            int i = idx++ % OPS_PER_THREAD;
            return hm.put(i, i);
        }
    }
}
