package collection;

import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Practice_TreeSet {
    private static final int SIZE = 500_000;

    public static void main(String[] args) {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 자동 정렬 집합 / 범위 검색·최솟값·최댓값 조회
    static void demonstratePurpose() {
        System.out.println("=== [용도] TreeSet ===");

        TreeSet<Integer> set = new TreeSet<>(List.of(5, 3, 8, 1, 9, 2, 7, 4, 6));
        System.out.println("삽입 원소      : [5, 3, 8, 1, 9, 2, 7, 4, 6]");
        System.out.println("TreeSet 순회   : " + set + "  (자동 오름차순 정렬)");

        System.out.println("first()        : " + set.first()     + "  (최솟값)");
        System.out.println("last()         : " + set.last()      + "  (최댓값)");
        System.out.println("floor(6)       : " + set.floor(6)    + "  (6 이하 최댓값)");
        System.out.println("ceiling(6)     : " + set.ceiling(6)  + "  (6 이상 최솟값)");
        System.out.println("lower(6)       : " + set.lower(6)    + "  (6 미만 최댓값)");
        System.out.println("higher(6)      : " + set.higher(6)   + "  (6 초과 최솟값)");
        System.out.println("subSet(3,7)    : " + set.subSet(3, 7)   + "  (3 이상 7 미만)");
        System.out.println("headSet(5)     : " + set.headSet(5)     + "  (5 미만)");
        System.out.println("tailSet(5)     : " + set.tailSet(5)     + "  (5 이상)");
        System.out.println("descendingSet(): " + set.descendingSet() + "  (역순)");
        System.out.println();
    }

    // 장점: 항상 정렬 유지(Red-Black Tree) / 범위 연산 O(log n)
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] TreeSet ===");
        TreeSet<Integer> set = new TreeSet<>();
        for (int i = SIZE; i >= 1; i--) set.add(i); // 역순 삽입

        System.out.println("역순 삽입 후 first() : " + set.first() + "  (항상 최솟값)");
        System.out.println("역순 삽입 후 last()  : " + set.last()  + "  (항상 최댓값)");

        SortedSet<Integer> range = set.subSet(100, 200);
        System.out.println("subSet(100,200) 크기 : " + range.size() + "  (범위 뷰, 별도 복사 없음)");
        System.out.println();
    }

    // 단점: 모든 연산 O(log n) — HashSet의 O(1) 대비 느림 / null 삽입 불가(Comparator 없을 때)
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] TreeSet ===");

        TreeSet<Integer> set = new TreeSet<>();
        for (int i = 0; i < SIZE; i++) set.add(i);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) set.contains(i);
        long elapsed = System.nanoTime() - start;
        System.out.println("contains() " + SIZE + "회 : " + elapsed / 1_000_000 + " ms  (O(log n) — 트리 탐색)");

        // null 삽입 불가
        try {
            set.add(null);
        } catch (NullPointerException e) {
            System.out.println("null 삽입 시도      : NullPointerException  (자연 순서 비교 불가)");
        }
        System.out.println();
    }

    // 성능: vs HashSet — add/contains 속도 (HashSet O(1) vs TreeSet O(log n))
    static void demonstratePerformance() {
        System.out.println("=== [성능] TreeSet vs HashSet ===");

        TreeSet<Integer> treeSet = new TreeSet<>();
        HashSet<Integer> hashSet = new HashSet<>(SIZE * 2);

        // add 비교
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) treeSet.add(i);
        long treeAdd = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) hashSet.add(i);
        long hashAdd = System.nanoTime() - start;

        System.out.printf("add()       %,d회 → TreeSet: %,d ns  HashSet: %,d ns%n", SIZE, treeAdd, hashAdd);

        // contains 비교
        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) treeSet.contains(i);
        long treeContains = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) hashSet.contains(i);
        long hashContains = System.nanoTime() - start;

        System.out.printf("contains()  %,d회 → TreeSet: %,d ns  HashSet: %,d ns%n", SIZE, treeContains, hashContains);
        System.out.println("→ TreeSet은 정렬 유지 비용(Red-Black Tree 회전)으로 HashSet 대비 느림");
        System.out.println();
    }
}
