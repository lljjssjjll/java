# PriorityQueue

## 용도
- **우선순위가 높은 원소를 먼저 처리**해야 할 때
- 다익스트라·A* 알고리즘의 우선순위 큐
- 작업 스케줄링(중요도 순 처리)
- 스트림의 **Top-K 원소** 추출
- 기본: 자연 순서(오름차순, 최솟값 우선) / `Comparator`로 커스텀 가능
- 내부: **이진 최소 힙(binary min-heap, 배열 기반)**

## 장점
| 항목 | 내용 |
|------|------|
| **peek() O(1)** | 힙 루트(인덱스 0)가 항상 최솟값 — 단순 배열 접근 |
| **offer/poll O(log n)** | 힙 삽입·삭제 후 sift-up·sift-down으로 힙 재구성 |
| **배열 기반** | 노드 기반 TreeSet보다 메모리 연속성으로 캐시 효율 우수 |

## 단점
| 항목 | 내용 |
|------|------|
| **iterator 순서 미보장** | `for-each` 순회는 내부 배열 순서 (정렬 순서 아님) |
| **contains/remove O(n)** | 힙 전체를 선형 탐색해야 함 |
| **null 불허** | `offer(null)` → `NullPointerException` |
| **동기화 없음** | 멀티스레드 환경에서 `PriorityBlockingQueue` 사용 필요 |

## 성능

| 연산 | PriorityQueue | TreeSet | 비고 |
|------|--------------|---------|------|
| `peek()/first()` | **O(1)** | O(1) | 힙 루트 vs 트리 최좌측 노드 |
| `offer()/add()` | **O(log n)** | O(log n) | sift-up vs Red-Black Tree 삽입 |
| `poll()/pollFirst()` | **O(log n)** | O(log n) | sift-down vs 트리 삭제 |
| `contains()` | O(n) | **O(log n)** | 선형 탐색 vs 트리 탐색 |
| 정렬 순회 | X | **O(n)** | iterator 순서 미보장 |
| 실제 처리량 | **빠름** | 느림 | 배열 기반 힙의 캐시 효율 우위 |

> **결론**: 최솟값(최댓값)을 반복 추출 → PriorityQueue / 전체 정렬 순회·범위 탐색 → TreeSet
