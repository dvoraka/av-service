# Performance notes

## Solr

### 1. milion
In collection: 10602
In Solr: 59769
After commit: 65834


#### commits
**100 milions** documents

x   | 10 | 100 | 1000 | 10 000 |
no  | 61 ms | 547 ms | 3.7 s | 21.9 s |
soft| x | 1.6 s | 11.3 s | 11 m 10.2 s |
hard| 8.5 s | 58.4 s | 9 m 42.1 s | x |
