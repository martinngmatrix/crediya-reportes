package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.report.Report;
import co.com.bancolombia.model.report.gateways.ReportRepository;
import lombok.extern.log4j.Log4j2;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;


@Repository
@Log4j2
public class DynamoDBTemplateAdapter extends TemplateAdapterOperations<Report /*domain model*/, String, ModelEntity /*adapter model*/>  implements ReportRepository  {
    private final DynamoDbEnhancedAsyncClient dynamoDbClient;

    public DynamoDBTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, Report.class /*domain model*/), "dyn-reports");
        this.dynamoDbClient = connectionFactory;
    }

    @Override
    public Mono<List<Report /*domain model*/>> getEntityBySomeKeys(String partitionKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey);
        return query(queryExpression);
    }

    public Mono<List<Report /*domain model*/>> getEntityBySomeKeysByIndex(String partitionKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey);
        return queryByIndex(queryExpression, "secondary_index" /*index is optional if you define in constructor*/);
    }

    private QueryEnhancedRequest generateQueryExpression(String partitionKey) {
        return QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(partitionKey).build()))
                .build();
    }

    @Override
    public Mono<Void> updateApprovedLoansApplicationsReport(String partitionKey, BigDecimal amount) {
        return getEntityBySomeKeys(partitionKey)
                .flatMap(list -> {
                    Report report;
                    if (list.isEmpty()) {
                        report = Report.builder()
                                .applicationStatus(partitionKey)
                                .totalAmount(amount)
                                .count(BigInteger.ONE)
                                .build();
                    } else {
                        report = list.get(0);
                        report.setTotalAmount(report.getTotalAmount().add(amount));
                        report.setCount(report.getCount().add(BigInteger.ONE));
                    }

                    ModelEntity entity = new ModelEntity(
                        report.getApplicationStatus(),
                        report.getCount(),
                        report.getTotalAmount()
                    );

                    return Mono.fromFuture(() ->
                            dynamoDbClient
                                    .table("dyn-reports", TableSchema.fromBean(ModelEntity.class))
                                    .putItem(entity)
                    ).then();
                });
    }
}
