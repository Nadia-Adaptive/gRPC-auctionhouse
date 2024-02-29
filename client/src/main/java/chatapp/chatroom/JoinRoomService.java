package chatapp.chatroom;

import chatapp.RoomService.ChatRoomServiceOuterClass.ChatRoomResponse;
import chatapp.RoomService.ChatRoomServiceOuterClass.JoinChatRoomRequest;
import chatapp.RoomService.ReactorChatRoomServiceGrpc;
import chatapp.connection.ChatChannel;
import chatapp.connection.ChatConnection;
import chatapp.service.ReactiveService;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static chatapp.chatroom.ChatRoomMapper.mapToChatRoom;

public class JoinRoomService implements ReactiveService<ChatRoom> {
    private final Channel channel;
    private Consumer<ChatRoom> callback;
    private Consumer<Throwable> errorHandler;
    private Disposable response;

    public JoinRoomService() {
        this.channel = ChatChannel.getChannel();
    }

    public void processResponse(final ChatRoomResponse r) {
        if (callback != null) {
            callback.accept(mapToChatRoom(r));
        }
    }

    public void joinRoom(final int roomId) {
        ChatConnection.roomId = roomId;
        final var stub = ReactorChatRoomServiceGrpc.newReactorStub(channel);

        try {
            processResponse(Mono
                    .just(JoinChatRoomRequest.newBuilder()
                            .setRoomId(roomId)
                            .build())
                    .transform(stub::joinChatRoom)
                    .block());
        } catch (final StatusRuntimeException e) {
            errorHandler.accept(e);
        }
    }

    @Override
    public void setErrorHandler(final Consumer<Throwable> t) {
        this.errorHandler = t;
    }

    @Override
    public void setSuccessHandler(final Consumer<ChatRoom> c) {
        callback = c;
    }

    @Override
    public void closeService() {
        response.dispose();
    }
}
