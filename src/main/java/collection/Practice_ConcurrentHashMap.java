package collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Practice_ConcurrentHashMap {
    private static final int THREADS = 8;
    private static final int OPS_PER_THREAD = 50_000;

    public static void main(String[] args) throws InterruptedException {
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

    // 성능: vs Collections.synchronizedMap — 멀티스레드 put 경쟁 비교
    static void demonstratePerformance() throws InterruptedException {
        System.out.println("=== [성능] ConcurrentHashMap vs synchronizedMap (멀티스레드) ===");

        ConcurrentHashMap<Integer, Integer> chm = new ConcurrentHashMap<>();
        Map<Integer, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());

        // ConcurrentHashMap
        CountDownLatch latch1 = new CountDownLatch(THREADS);
        Thread[] t1 = new Thread[THREADS];
        long start = System.nanoTime();
        for (int t = 0; t < THREADS; t++) {
            final int base = t * OPS_PER_THREAD;
            t1[t] = new Thread(() -> {
                for (int i = 0; i < OPS_PER_THREAD; i++) chm.put(base + i, i);
                latch1.countDown();
            });
            t1[t].start();
        }
        latch1.await();
        long chmTime = System.nanoTime() - start;

        // synchronizedMap
        CountDownLatch latch2 = new CountDownLatch(THREADS);
        Thread[] t2 = new Thread[THREADS];
        start = System.nanoTime();
        for (int t = 0; t < THREADS; t++) {
            final int base = t * OPS_PER_THREAD;
            t2[t] = new Thread(() -> {
                for (int i = 0; i < OPS_PER_THREAD; i++) syncMap.put(base + i, i);
                latch2.countDown();
            });
            t2[t].start();
        }
        latch2.await();
        long syncTime = System.nanoTime() - start;

        System.out.printf("스레드 %d개 × put %,d회 → ConcurrentHashMap: %,d ns  synchronizedMap: %,d ns%n",
                THREADS, OPS_PER_THREAD, chmTime, syncTime);
        System.out.println("→ synchronizedMap은 단일 락으로 직렬화 / ConcurrentHashMap은 버킷 단위 병렬 처리");
        System.out.println();
    }
}
