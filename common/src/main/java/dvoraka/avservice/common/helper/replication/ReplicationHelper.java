package dvoraka.avservice.common.helper.replication;

import dvoraka.avservice.common.data.Command;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.common.data.replication.DefaultReplicationMessage;
import dvoraka.avservice.common.data.replication.MessageRouting;
import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.data.replication.ReplicationStatus;
import dvoraka.avservice.common.helper.UuidHelper;

/**
 * Replication helper interface.
 */
public interface ReplicationHelper extends ReplicationServiceHelper, UuidHelper {

    default ReplicationMessage createNoDataMessage(
            FileMessage message, Command command, String fromNode, String toNode
    ) {
        return new DefaultReplicationMessage.Builder(message.getId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(command)
                .toId(toNode)
                .fromId(fromNode)
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createDataMessage(
            FileMessage message, Command command, String fromNode, String toNode
    ) {
        return new DefaultReplicationMessage.Builder(message.getId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(command)
                .toId(toNode)
                .fromId(fromNode)
                .data(message.getData())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createResponse(
            ReplicationMessage message, ReplicationStatus status, String fromNode
    ) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(message.getCommand())
                .replicationStatus(status)
                .toId(message.getFromId())
                .fromId(fromNode)
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createSuccessResponse(ReplicationMessage message, String fromNode) {
        return createResponse(message, ReplicationStatus.OK, fromNode);
    }

    default ReplicationMessage createFailedResponse(ReplicationMessage message, String fromNode) {
        return createResponse(message, ReplicationStatus.FAILED, fromNode);
    }

    default ReplicationMessage createSaveMessage(
            FileMessage message, String fromNode, String toNode
    ) {
        return createDataMessage(message, Command.SAVE, fromNode, toNode);
    }

    default ReplicationMessage createLoadMessage(
            FileMessage message, String fromNode, String toNode
    ) {
        return createNoDataMessage(message, Command.LOAD, fromNode, toNode);
    }

    default ReplicationMessage createLoadSuccess(
            FileMessage message, ReplicationMessage request, String fromNode
    ) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(request.getId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.LOAD)
                .replicationStatus(ReplicationStatus.OK)
                .toId(request.getFromId())
                .fromId(fromNode)
                .data(message.getData())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createUpdateMessage(
            FileMessage message, String fromNode, String toNode
    ) {
        return createDataMessage(message, Command.UPDATE, fromNode, toNode);
    }

    default ReplicationMessage createDeleteMessage(
            FileMessage message, String fromNode, String toNode
    ) {
        return createNoDataMessage(message, Command.DELETE, fromNode, toNode);
    }

    default ReplicationMessage createDiagnosticsMessage(String fromNode) {
        String uuid = genUuidStr();
        return new DefaultReplicationMessage.Builder(uuid)
                .type(MessageType.DIAGNOSTICS)
                .routing(MessageRouting.UNICAST)
                .correlationId(uuid)
                .fromId(fromNode)
                .toId(fromNode)
                .build();
    }

    default boolean isUnicast(ReplicationMessage message) {
        return message.getRouting() == MessageRouting.UNICAST;
    }

    default boolean isCommand(ReplicationMessage message, Command... commands) {
        for (Command command : commands) {
            if (message.getCommand() == command) {
                return true;
            }
        }

        return false;
    }
}
