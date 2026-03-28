package tech.itk.task.wire.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaTaskRoutes extends RouteBuilder {

  @Override
  public void configure() {
    // Маршрут для события создания задачи
    from("direct:task-created")
      .to("kafka:task-created?brokers={{kafka.bootstrap-servers}}");

    // Маршрут для события назначения исполнителя
    from("direct:task-assigned")
      .to("kafka:task-assigned?brokers={{kafka.bootstrap-servers}}");
  }
}
