package com.weareadaptive.auctionhouse;

import com.weareadaptive.auctionhouse.configuration.ApplicationContext;
import com.weareadaptive.auctionhouse.organisation.OrganisationGRPCController;
import com.weareadaptive.auctionhouse.server.mocks.MockClientHeaderInterceptor;
import com.weareadaptive.auctionhouse.user.UserGRPCController;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.jupiter.api.BeforeEach;

import static com.weareadaptive.auctionhouse.configuration.ApplicationContext.getApplicationContext;

public abstract class IntegrationTest {
    //private MockApplicationContext context;
    // TODO: Add the setup code below to MockApplicationContext
    //    userRepo = new UserRepository();
    //        userRepo.addUser("User01");
    //        roomRepo = new com.weareadaptive.server.room.ChatRoomRepository();

    protected ManagedChannel channel;
    private ApplicationContext context;
    protected IntegrationTestData testData;

    @BeforeEach
    public void setup() throws Exception {
        context = getApplicationContext();

        final var grpcCleanupRule = new GrpcCleanupRule();
        final var serverName = InProcessServerBuilder.generateName();

        grpcCleanupRule.register(
                InProcessServerBuilder.forName(serverName).directExecutor()
                        .addService(new OrganisationGRPCController())
                        .addService(new UserGRPCController())
                        .build()
                        .start());

        channel =
                grpcCleanupRule.register(InProcessChannelBuilder.forName(serverName).directExecutor()
                        .usePlaintext()
                        .intercept(new MockClientHeaderInterceptor())
                        .build());
        System.out.println(channel);
    }
}
