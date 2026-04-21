# TreeMap

## 용도
- **키 자동 정렬** 맵 (삽입 시 즉시 정렬)
- **최솟값·최댓값 키** 조회 (`firstKey()`, `lastKey()`)
- **범위 검색** (`subMap`, `headMap`, `tailMap`)
- **근사 키 탐색** (`floorKey`, `ceilingKey`, `lowerKey`, `higherKey`)
- 내부: **Red-Black Tree** (자가 균형 이진 탐색 트리)

## 장점
| 항목 | 내용 |
|------|------|
| **항상 정렬 유지** | 삽입 즉시 트리 재조정 → 별도 정렬 불필요 |
| **범위 뷰** | `subMap/headMap/tailMap` → 원본 공유 뷰 반환 (복사 없음) |
| **근사 탐색 O(log n)** | `floorKey(x)` = x 이하 최대 키, `ceilingKey(x)` = x 이상 최소 키 |
| **역순 지원** | `descendingMap()` / `descendingKeySet()` |

## 단점
| 항목 | 내용 |
|------|------|
| **모든 연산 O(log n)** | 트리 탐색·재조정으로 HashMap O(1) 대비 느림 |
| **null 키 불가** | 자연 순서 사용 시 `NullPointerException` |
| **메모리 오버헤드** | 노드당 `left + right + parent + color` 포인터 |

## 성능

| 연산 | TreeMap | HashMap | 비고 |
|------|---------|---------|------|
| `get(k)` | O(log n) | **O(1) 평균** | 트리 탐색 vs 해시 버킷 접근 |
| `put(k,v)` | O(log n) | **O(1) 평균** | 트리 삽입·재조정 vs 해시 버킷 삽입 |
| `remove(k)` | O(log n) | **O(1) 평균** | 트리 삭제·재조정 vs 해시 버킷 제거 |
| `firstKey()/lastKey()` | **O(1)** | X | 트리 가장 왼쪽/오른쪽 노드 |
| `floorKey/ceilingKey` | **O(log n)** | X | HashMap에 없는 기능 |
| `subMap(a,b)` | **O(log n)** | X | 뷰 반환, 복사 없음 |
| 정렬 순회 | **O(n)** | X | HashMap은 정렬 순회 불가 |

> **결론**: 단순 키-값 조회 → HashMap / 키 정렬·범위·최솟값·최댓값 필요 → TreeMap
