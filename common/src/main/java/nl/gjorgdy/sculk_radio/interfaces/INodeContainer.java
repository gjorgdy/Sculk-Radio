package nl.gjorgdy.sculk_radio.interfaces;

import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import org.jetbrains.annotations.Nullable;

public interface INodeContainer {

    void sculkRadio$setNode(Node node);

    @Nullable
    Node sculkRadio$getNode();

}
