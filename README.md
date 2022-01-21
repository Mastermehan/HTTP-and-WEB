Refactoring & MultiThreading
Легенда
Достаточно часто после того, как прототип проверен (мы про то, что было реализовано на лекции), возникает задача привести это в более-менее нормальный вид: выделить классы, методы, обеспечить должную функциональность.

Задача
Необходимо отрефакторить код, рассмотренный на лекции, и применить все те знания, которые у вас есть:

Выделить класс Server с методами для
запуска
обработки конкретного подключения
Реализовать обработку подключений с помощью ThreadPool'а (выделите фиксированный на 64 потока и каждое подключение обрабатывайте в потоке из пула)
Поскольку вы - главный архитектор и проектировщик данного небольшого класса, то все архитектурные решения принимать вам, но будьте готовы к критике со стороны проверяющих.
