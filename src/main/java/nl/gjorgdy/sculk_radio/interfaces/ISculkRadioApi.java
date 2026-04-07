package nl.gjorgdy.sculk_radio.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.objects.Node;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface ISculkRadioApi {

    /**
     * Check if a Jukebox is a radio that can connect to Note Blocks
     *
     * @param world the world the Jukebox is in
     * @param pos   the Jukebox's position
     * @return true if the Jukebox is a radio, false if it is not
     */
    boolean isRadio(ServerLevel world, BlockPos pos);

    /**
     * Connect a Jukebox to Note Blocks and run a callback on it and the connected Note Blocks
     *
     * @param world              the world the Jukebox is in
     * @param pos                the Jukebox's position
     * @param connectCallback    the callback to run on the Jukebox and Note Blocks
     * @param disconnectCallback the callback to run on the Jukebox and Note Blocks when the sound stops playing
     * @return true if the callback was run, false if there is no Jukebox at the given position
     */
    default boolean connect(ServerLevel world, BlockPos pos, Consumer<Node> connectCallback, Consumer<Node> disconnectCallback) {
        return connect(world, pos, connectCallback, disconnectCallback, n -> {
        });
    }

    /**
     * Connect a Jukebox to Note Blocks and run a callback on it and the connected Note Blocks
     *
     * @param world              the world the Jukebox is in
     * @param pos                the Jukebox's position
     * @param connectCallback    the callback to run on the Jukebox and Note Blocks
     * @param disconnectCallback the callback to run on the Jukebox and Note Blocks when the sound stops playing
     * @param tickCallback       the callback to run on the Jukebox and Note Blocks every tick
     * @return true if the callback was run, false if there is no Jukebox at the given position
     */
    boolean connect(ServerLevel world, BlockPos pos, Consumer<Node> connectCallback, Consumer<Node> disconnectCallback, Consumer<Node> tickCallback);

    /**
     * Disconnected a Jukebox or Note Block and its connected Note Blocks
     *
     * @param world the world the Jukebox is in
     * @param pos   the Jukebox's position
     * @return true if the Note Blocks were disconnected, false if there is no Jukebox or Note Block at the given position
     */
    boolean disconnect(ServerLevel world, BlockPos pos);

    /**
     * Execute a tick on a Jukebox and connected Note Blocks for visual effects
     *
     * @param world the world the Jukebox is in
     * @param pos   the Jukebox's position
     * @return true if the tick was executed, false if the tick could not be executed
     */
    boolean tick(ServerLevel world, BlockPos pos);

}
