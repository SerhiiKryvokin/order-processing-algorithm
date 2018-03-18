#### order-processing-algorithm

Minimum/maximum prices are maintained in indexed priority queues, that allow to remove any price with logarithmic complexity.  
Default java implementation of priority queue has O(n) complexity for remove(element) operation. 
It is possible to remove elements lazily, just marking them as deleted, but it may cause big amount of useless elements in the queue and slow down get-min operation.  

Resting orders are processed in 'first came first processed' way. Resting orders ids are stored in LinkedHashSet per price that allows to iterate over ids in the order they were added. It also allows to cancel an order in O(1) in non-lazy way.
