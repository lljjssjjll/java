package collection;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class Practice_LinkedHashSet {
    private static final int SIZE = 500_000;

    public static void main(String[] args) {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 삽입 순서를 유지하면서 중복을 허용하지 않는 집합
    static void demonstratePurpose() {
        System.out.println("=== [용도] LinkedHashSet ===");

        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add("banana"); set.add("apple"); set.add("cherry"); set.add("date");
        System.out.println("삽입 순서 : banana → apple → cherry → date");
        System.out.println("순회 결과 : " + set + "  (삽입 순서 유지)");

        boolean added = set.add("apple");
        System.out.println("중복 추가(apple)     : " + added + "  (이미 존재, 순서 변경 없음)");
        System.out.println("중복 추가 후 순회    : " + set);

        // 방문 이력 추적 — 순서 있는 중복 없는 집합이 필요한 경우
        LinkedHashSet<String> visited = new LinkedHashSet<>();
        List<String> path = List.of("home", "about", "home", "contact", "about");
        for (String page : path) visited.add(page);
        System.out.println("방문 경로(중복 포함) : " + path);
        System.out.println("방문 이력(중복 제거) : " + visited);
        System.out.println();
    }

    // 장점: 삽입 순서 유지 + HashSet 수준의 O(1) add/contains/remove
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] LinkedHashSet ===");
        LinkedHashSet<Integer> set = new LinkedHashSet<>(SIZE * 2);
        for (int i = 0; i < SIZE; i++) set.add(i);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) set.contains(i);
        long elapsed = System.nanoTime() - start;
        System.out.println("contains() " + SIZE + "회 : " + elapsed / 1_000_000 + " ms  (O(1) 평균, 순서 유지 상태)");
        System.out.println();
    }

    // 단점: HashSet 대비 메모리 오버헤드 — 각 버킷 엔트리에 prev·next 포인터 추가
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] LinkedHashSet ===");
        System.out.println("내부 구조: HashMap 버킷 엔트리 + 이중 연결 리스트(prev·next)");
        System.out.println("HashSet 엔트리  : [hash | key | value | next]");
        System.out.println("LinkedHashSet   : [hash | key | value | next | before | after]");
        System.out.println("→ 엔트리당 포인터 2개 추가 → HashSet 대비 메모리 증가");

        // 삽입 순서는 유지하지만 접근 순서(LRU 식) 변경은 지원 안 함
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add("A"); set.add("B"); set.add("C");
        set.contains("A"); // 접근해도 순서 변화 없음
        System.out.println("A 접근 후 순서   : " + set + "  (접근 순서 재정렬 불가)");
        System.out.println();
    }

    // 성능: vs HashSet — add/contains 속도 비교 (LinkedHashSet이 미미하게 느림)
    static void demonstratePerformance() {
        System.out.println("=== [성능] LinkedHashSet vs HashSet ===");

        HashSet<Integer> hashSet = new HashSet<>(SIZE * 2);
        LinkedHashSet<Integer> linkedSet = new LinkedHashSet<>(SIZE * 2);

        // add 비교
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) hashSet.add(i);
        long hashAdd = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) linkedSet.add(i);
        long linkedAdd = System.nanoTime() - start;

        System.out.printf("add()       %,d회 → HashSet: %,d ns  LinkedHashSet: %,d ns%n", SIZE, hashAdd, linkedAdd);

        // contains 비교
        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) hashSet.contains(i);
        long hashContains = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) linkedSet.contains(i);
        long linkedContains = System.nanoTime() - start;

        System.out.printf("contains()  %,d회 → HashSet: %,d ns  LinkedHashSet: %,d ns%n", SIZE, hashContains, linkedContains);
        System.out.println("→ 연결 리스트 포인터 유지 비용으로 LinkedHashSet이 미미하게 느림");
        System.out.println();
    }
}
