package collection;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Practice_LinkedHashMap {
    private static final int SIZE = 500_000;

    public static void main(String[] args) {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 삽입 순서 유지 맵 / accessOrder=true 로 LRU 캐시 구현
    static void demonstratePurpose() {
        System.out.println("=== [용도] LinkedHashMap ===");

        // 삽입 순서 유지
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("banana", 2); map.put("apple", 1); map.put("cherry", 3);
        System.out.println("삽입 순서: banana → apple → cherry");
        System.out.println("entrySet  : " + map.entrySet() + "  (삽입 순서 유지)");

        map.put("apple", 99); // 재삽입해도 순서 유지
        System.out.println("apple 갱신 후: " + map.entrySet() + "  (순서 변경 없음)");

        // LRU 캐시: accessOrder=true + removeEldestEntry 오버라이드
        final int CAPACITY = 3;
        LinkedHashMap<Integer, String> lruCache = new LinkedHashMap<>(CAPACITY, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
                return size() > CAPACITY;
            }
        };
        lruCache.put(1, "A"); lruCache.put(2, "B"); lruCache.put(3, "C");
        System.out.println("캐시 초기            : " + lruCache.keySet());

        lruCache.get(1);                     // 1 접근 → 최근 사용으로 이동
        System.out.println("get(1) 후            : " + lruCache.keySet() + "  (1이 뒤로 이동)");

        lruCache.put(4, "D");                // 용량 초과 → 가장 오래된 2 제거
        System.out.println("put(4) 후(용량=" + CAPACITY + ")  : " + lruCache.keySet() + "  (2가 제거됨)");
        System.out.println();
    }

    // 장점: HashMap 수준의 O(1) 연산 + 삽입/접근 순서 유지
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] LinkedHashMap ===");
        LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>(SIZE * 2);
        for (int i = 0; i < SIZE; i++) map.put(i, i);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) map.get(i);
        long elapsed = System.nanoTime() - start;
        System.out.println("get() " + SIZE + "회 : " + elapsed / 1_000_000 + " ms  (O(1), 순서 유지 상태)");
        System.out.println();
    }

    // 단점: HashMap 대비 메모리 오버헤드 — 각 엔트리에 before·after 포인터 추가
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] LinkedHashMap ===");
        System.out.println("HashMap 엔트리     : [hash | key | value | next]");
        System.out.println("LinkedHashMap 엔트리: [hash | key | value | next | before | after]");
        System.out.println("→ 엔트리당 포인터 2개 추가, 삽입·삭제 시 연결 리스트 포인터 갱신 비용");

        // 접근 순서 기록은 accessOrder=true 일 때만 동작 (기본값=false)
        LinkedHashMap<String, Integer> defaultMap = new LinkedHashMap<>();
        defaultMap.put("A", 1); defaultMap.put("B", 2); defaultMap.put("C", 3);
        defaultMap.get("A"); // 기본(삽입 순서 모드): 접근해도 순서 무변화
        System.out.println("삽입 순서 모드 get(A) 후: " + defaultMap.keySet() + "  (순서 유지)");
        System.out.println("→ LRU 기능은 accessOrder=true 생성자 옵션 필요");
        System.out.println();
    }

    // 성능: vs HashMap — put/get 속도 (LinkedHashMap이 포인터 유지 비용으로 미미하게 느림)
    static void demonstratePerformance() {
        System.out.println("=== [성능] LinkedHashMap vs HashMap ===");

        HashMap<Integer, Integer> hashMap = new HashMap<>(SIZE * 2);
        LinkedHashMap<Integer, Integer> linkedMap = new LinkedHashMap<>(SIZE * 2);

        // put 비교
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) hashMap.put(i, i);
        long hashPut = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) linkedMap.put(i, i);
        long linkedPut = System.nanoTime() - start;

        System.out.printf("put() %,d회 → HashMap: %,d ns  LinkedHashMap: %,d ns%n", SIZE, hashPut, linkedPut);

        // get 비교
        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) hashMap.get(i);
        long hashGet = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) linkedMap.get(i);
        long linkedGet = System.nanoTime() - start;

        System.out.printf("get() %,d회 → HashMap: %,d ns  LinkedHashMap: %,d ns%n", SIZE, hashGet, linkedGet);
        System.out.println("→ 삽입·삭제마다 연결 리스트 포인터 갱신이 추가되어 LinkedHashMap이 미미하게 느림");
        System.out.println();
    }
}
