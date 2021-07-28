# Flink Demo for Voxxed Days Australia 2021

This is a sample app that makes use of Flink. It demonstrates basic concepts and idioms of streaming applications.
These concepts can also be generalized to other streaming frameworks.

## Use Case

Process ledger events on an hourly basis, grouped by banks. Add up the amounts and generate bank settlement events.

This solutions uses the following event streaming concepts and patterns:

### Event Time vs Processing Time

* Event time: refers to the time when the event actually occurred.
* Processing time: refers to the time the system observes the event.

For this use case we decided to use the event time to have more accurate representation of the actual events in the window.

### Windowed Aggregations

[Windows](https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/datastream/operators/windows/#windows) are central concepts in stream processing. Windows devied an infinite streams into buckets that are configured according to
the windows semantics:

* Tumbling window
* Sliding window
* Session window
* Global window

This use case requires to run some aggregation every hour. Thus, we defined a tumbling event window of one hour.

You can find in the documentation more detailed explanation of [Flink's Windows features](https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/datastream/operators/windows/#windows).

To process the events in the window we use an [AggregateFunction](https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/datastream/operators/windows/#aggregatefunction). An ```AggregateFunction``` should be familiar for those coming from a functional programming background.
It has an accumulator, an input and a result. In our case we want to:

* provide an aggregate of the total amount transacted for a given bank within the window.
* output the start and end time of the events in the window to help during verification of the output using external sources: data warehouse query.

Simpler queries can use a [ReduceFunction](https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/datastream/operators/windows/#reducefunction) which allows
to codify how two elements should be processed.

### Watermarks

Watermarks define when a window is complete. It uses the event time and relies on the notion that events can be delayed/reordered due to multiple causes in a distributed system.

In this application we defined a watermark of 2 hours lateness that indicates that the window will be closed after that time. Any event that arrives past the watermark will
be dropped. Alternatively Flink allows to keep track of these type of events using [side outputs](https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/datastream/operators/windows/#getting-late-data-as-a-side-output).

```Kotlin
.assignTimestampsAndWatermarks(
        WatermarkStrategy
          .forBoundedOutOfOrderness<ReceiverRecord<LedgerEvent>>(Duration.ofHours(2))
          .withTimestampAssigner { x, _ -> x.data.occurred_at }
      )
```

### Source and Sinks

Flink allows configuring a wide range of sources and sinks. In general a Flink application will read from a stream generating source:
Kafka, socket, file. If the result of the processing should be shared to more than one application or consumer, we recommend using a
Kafka sink. This approach is demonstrated in this application.

## Building


