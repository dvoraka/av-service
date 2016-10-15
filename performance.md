# Performance notes

Based on PC3

## Solr

Documents are Message info documents.

### 1. milion
In collection: 10602 ms
In Solr: 59769 ms
After commit: 65834 ms


#### commits

With empty collection.

 x  | 10 | 100 | 1000 | 10 000 
--- | --- | --- | --- | ---
no  | 67 ms | 396 ms | 3 s | 21.4 s 
soft| 149 ms | 879 ms | 7.9 s | 1 m 6 s 
hard| 4.2 s | 29.8 s | 4 m 46 s | x 

With **100 milions** documents.

 x  | 10 | 100 | 1000 | 10 000 
--- | --- | --- | --- | ---
no  | 61 ms | 547 ms | 3.7 s | 21.9 s 
soft| x | 1.6 s | 11.3 s | 11 m 10 s 
hard| 8.5 s | 58.4 s | 9 m 42.1 s | x 
