package collection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

public class Practice_PriorityQueue {
    private static final int SIZE = 500_000;

    public static void main(String[] args) {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 우선순위 기반 처리 — 항상 최솟값(기본) 또는 최댓값을 먼저 꺼냄
    static void demonstratePurpose() {
        System.out.println("=== [용도] PriorityQueue ===");

        // 기본: 자연 순서(오름차순, 최솟값 우선)
        PriorityQueue<Integer> minPQ = new PriorityQueue<>();
        minPQ.offer(5); minPQ.offer(1); minPQ.offer(3); minPQ.offer(2); minPQ.offer(4);
        System.out.println("삽입: [5,1,3,2,4]");
        System.out.print("poll 순서(최솟값 우선): ");
        while (!minPQ.isEmpty()) System.out.print(minPQ.poll() + " ");
        System.out.println();

        // 최댓값 우선 (역순 Comparator)
        PriorityQueue<Integer> maxPQ = new PriorityQueue<>(Comparator.reverseOrder());
        maxPQ.offer(5); maxPQ.offer(1); maxPQ.offer(3); maxPQ.offer(2); maxPQ.offer(4);
        System.out.print("poll 순서(최댓값 우선): ");
        while (!maxPQ.isEmpty()) System.out.print(maxPQ.poll() + " ");
        System.out.println();

        // 객체 우선순위 정렬
        record Task(String name, int priority) {}
        PriorityQueue<Task> taskPQ = new PriorityQueue<>(Comparator.comparingInt(Task::priority));
        taskPQ.offer(new Task("low",    3));
        taskPQ.offer(new Task("high",   1));
        taskPQ.offer(new Task("medium", 2));
        System.out.print("작업 처리 순서: ");
        while (!taskPQ.isEmpty()) System.out.print(taskPQ.poll().name() + " ");
        System.out.println();
        System.out.println();
    }

    // 장점: peek O(1) — 힙 루트가 항상 최솟값 / offer·poll O(log n)
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] PriorityQueue ===");
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int i = SIZE; i >= 1; i--) pq.offer(i);

        // O(1) 최솟값 조회 — 힙 루트 직접 참조
        long start = System.nanoTime();
        int min = pq.peek();
        long peekTime = System.nanoTime() - start;
        System.out.println("peek() 결과 : " + min + "  소요 시간: " + peekTime + " ns  (O(1))");

        // O(log n) poll
        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) pq.offer(i);
        long offerTime = System.nanoTime() - start;
        System.out.println("offer() " + SIZE + "회 : " + offerTime / 1_000_000 + " ms  (O(log n))");
        System.out.println();
    }

    // 단점: 이터레이터 순회 시 정렬 순서 미보장 / contains·remove O(n) 선형 탐색
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] PriorityQueue ===");

        PriorityQueue<Integer> pq = new PriorityQueue<>(List.of(5, 1, 3, 2, 4));

        // iterator는 힙 내부 배열 순서(정렬 미보장)
        List<Integer> iterOrder = new ArrayList<>(pq);
        System.out.println("내부 배열 순서(iterator): " + iterOrder + "  (정렬 순서 아님)");
        System.out.println("peek()                  : " + pq.peek()    + "  (최솟값만 보장)");

        // contains/remove: 힙 전체 선형 탐색 → O(n)
        long start = System.nanoTime();
        boolean found = pq.contains(3);
        long elapsed = System.nanoTime() - start;
        System.out.println("contains(3) : " + found + "  (O(n) 선형 탐색)  " + elapsed + " ns");
        System.out.println();
    }

    // 성능: vs TreeSet — offer/poll 비교 (우선순위 큐 용도)
    static void demonstratePerformance() {
        System.out.println("=== [성능] PriorityQueue vs TreeSet ===");

        PriorityQueue<Integer> pq = new PriorityQueue<>(SIZE);
        TreeSet<Integer> ts = new TreeSet<>();

        // offer(add) 비교
        long start = System.nanoTime();
        for (int i = SIZE; i >= 1; i--) pq.offer(i);
        long pqOffer = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = SIZE; i >= 1; i--) ts.add(i);
        long tsAdd = System.nanoTime() - start;

        System.out.printf("offer/add() %,d회 → PriorityQueue: %,d ns  TreeSet: %,d ns%n", SIZE, pqOffer, tsAdd);

        // poll(pollFirst) 비교
        start = System.nanoTime();
        while (!pq.isEmpty()) pq.poll();
        long pqPoll = System.nanoTime() - start;

        start = System.nanoTime();
        while (!ts.isEmpty()) ts.pollFirst();
        long tsPoll = System.nanoTime() - start;

        System.out.printf("poll()      %,d회 → PriorityQueue: %,d ns  TreeSet: %,d ns%n", SIZE, pqPoll, tsPoll);
        System.out.println("→ PriorityQueue는 이진 힙(배열 기반) / TreeSet은 Red-Black Tree(노드 기반)");
        System.out.println("→ 단순 최솟값 반복 추출 용도에서는 PriorityQueue가 캐시 효율 면에서 유리");
        System.out.println();
    }
}
