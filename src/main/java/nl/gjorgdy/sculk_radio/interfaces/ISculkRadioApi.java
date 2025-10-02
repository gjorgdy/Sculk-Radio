package nl.gjorgdy.sculk_radio.interfaces;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.objects.Node;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface ISculkRadioApi {

    /**
     * Check if a jukebox is a radio that can connect to note-blocks
     *
     * @param world the world the jukebox is in
     * @param pos   the jukebox's position
     * @return true if the jukebox is a radio, false if it is not
     */
    boolean isRadio(ServerWorld world, BlockPos pos);

    /**
     * Connect a jukebox to noteblocks and run a callback on it and the connected note-blocks
     *
     * @param world              the world the jukebox is in
     * @param pos                the jukebox's position
     * @param connectCallback    the callback to run on the jukebox and note-blocks
     * @param disconnectCallback the callback to run on the jukebox and note-blocks when the sound stops playing
     * @return true if the callback was run, false if there is no jukebox at the given position
     */
    default boolean connect(ServerWorld world, BlockPos pos, Consumer<Node> connectCallback, Consumer<Node> disconnectCallback) {
        return connect(world, pos, connectCallback, disconnectCallback, n -> {
        });
    }

    /**
     * Connect a jukebox to noteblocks and run a callback on it and the connected note-blocks
     *
     * @param world              the world the jukebox is in
     * @param pos                the jukebox's position
     * @param connectCallback    the callback to run on the jukebox and note-blocks
     * @param disconnectCallback the callback to run on the jukebox and note-blocks when the sound stops playing
     * @param tickCallback       the callback to run on the jukebox and note-blocks every tick
     * @return true if the callback was run, false if there is no jukebox at the given position
     */
    boolean connect(ServerWorld world, BlockPos pos, Consumer<Node> connectCallback, Consumer<Node> disconnectCallback, Consumer<Node> tickCallback);

    /**
     * Disconnected a jukebox or note-block and its connected noteblocks
     *
     * @param world the world the jukebox is in
     * @param pos   the jukebox's position
     * @return true if the note-blocks were disconnected, false if there is no jukebox or note-block at the given position
     */
    boolean disconnect(ServerWorld world, BlockPos pos);

    /**
     * Execute a tick on a jukebox and connected note-blocks for visual effects
     *
     * @param world the world the jukebox is in
     * @param pos   the jukebox's position
     * @return true if the tick was executed, false if the tick could not be executed
     */
    boolean tick(ServerWorld world, BlockPos pos);

}
