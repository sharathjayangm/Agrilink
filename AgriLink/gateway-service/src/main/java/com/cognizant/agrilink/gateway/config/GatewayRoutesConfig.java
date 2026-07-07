package com.cognizant.agrilink.gateway.config;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Static, path-based routing for the AgriLink API gateway.
 *
 * Every request that arrives at the gateway (port 8080) with the prefix
 * {@code /agrilink/<module>/**} is forwarded — path preserved — to the
 * matching microservice on its own port. Because each microservice uses the
 * context path {@code /agrilink/<module>}, the full incoming path resolves
 * correctly on the downstream service.
 */
@Configuration
public class GatewayRoutesConfig {

	@Value("${agrilink.services.iam}")
	private String iamUri;

	@Value("${agrilink.services.farmer}")
	private String farmerUri;

	@Value("${agrilink.services.crop}")
	private String cropUri;

	@Value("${agrilink.services.input}")
	private String inputUri;

	@Value("${agrilink.services.subsidy}")
	private String subsidyUri;

	@Value("${agrilink.services.produce}")
	private String produceUri;

	@Value("${agrilink.services.report}")
	private String reportUri;

	@Value("${agrilink.services.notification}")
	private String notificationUri;

	@Bean
	public RouterFunction<ServerResponse> iamRoute() {
		return route("iam-service")
				.route(path("/agriLink/**"), http())
				.before(uri(iamUri))
				.build();
	}

	@Bean
	public RouterFunction<ServerResponse> farmerRoute() {
		return route("farmer-service")
				.route(path("/agrilink/farmer/**"), http())
				.before(uri(farmerUri))
				.build();
	}

	@Bean
	public RouterFunction<ServerResponse> cropRoute() {
		return route("crop-service")
				.route(path("/agrilink/crop/**"), http())
				.before(uri(cropUri))
				.build();
	}

	@Bean
	public RouterFunction<ServerResponse> inputRoute() {
		return route("input-service")
				.route(path("/agrilink/input/**"), http())
				.before(uri(inputUri))
				.build();
	}

	@Bean
	public RouterFunction<ServerResponse> subsidyRoute() {
		return route("subsidy-service")
				.route(path("/agrilink/subsidy/**"), http())
				.before(uri(subsidyUri))
				.build();
	}

	@Bean
	public RouterFunction<ServerResponse> produceRoute() {
		return route("produce-service")
				.route(path("/agrilink/produce/**"), http())
				.before(uri(produceUri))
				.build();
	}

	@Bean
	public RouterFunction<ServerResponse> reportRoute() {
		return route("report-service")
				.route(path("/agrilink/report/**"), http())
				.before(uri(reportUri))
				.build();
	}

	@Bean
	public RouterFunction<ServerResponse> notificationRoute() {
		return route("notification-service")
				.route(path("/agrilink/notification/**"), http())
				.before(uri(notificationUri))
				.build();
	}
}
