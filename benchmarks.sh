mvn clean package
java -jar target/benchmarks.jar ToyDisruptorBenchmark -i 3 -wi 1 -f 1


java -jar target/benchmarks.jar ToyDisruptorBenchmark -i 3 -wi 1 -f 1 -prof async:libPath=/Users/alex/Code/async-profiler/build/lib/libasyncProfiler.dylib


java -jar target/benchmarks.jar ToyDisruptorBenchmark.testToyDisruptor \
  -i 3 -wi 1 -f 1 \
  -prof async:libPath=/Users/alex/Code/async-profiler/build/lib;output=flamegraph;dir=./profiles

java -jar target/benchmarks.jar ToyDisruptorBenchmark.testRealDisruptor \
    -i 3 -wi 1 -f 1 \
    -prof async:libPath=/Users/alex/Code/async-profiler/build/lib;output=flamegraph;dir=./profiles