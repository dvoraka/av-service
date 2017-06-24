package dvoraka.avservice.common.replication;

import dvoraka.avservice.common.data.Command;
import dvoraka.avservice.common.data.DefaultReplicationMessage;
import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageRouting;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.common.data.ReplicationMessage;
import dvoraka.avservice.common.data.ReplicationStatus;

/**
 * Replication helper interface.
 */
//TODO: refactoring
public interface ReplicationHelper extends ReplicationServiceHelper {

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

    default ReplicationMessage createSaveMessage(
            FileMessage message, String fromNode, String toNode
    ) {
        return createDataMessage(message, Command.SAVE, fromNode, toNode);
    }

    default ReplicationMessage createSuccessResponse(ReplicationMessage message, String fromNode) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(message.getCommand())
                .replicationStatus(ReplicationStatus.OK)
                .toId(message.getFromId())
                .fromId(fromNode)
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createSaveSuccess(ReplicationMessage message, String fromNode) {
        return createSuccessResponse(message, fromNode);
    }

    default ReplicationMessage createSaveFailed(ReplicationMessage message, String fromNode) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.SAVE)
                .replicationStatus(ReplicationStatus.FAILED)
                .toId(message.getFromId())
                .fromId(fromNode)
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
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

    default ReplicationMessage createLoadFailed(
            FileMessage message, String fromNode, String toNode
    ) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getCorrelationId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.LOAD)
                .replicationStatus(ReplicationStatus.FAILED)
                .toId(toNode)
                .fromId(fromNode)
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

    default ReplicationMessage createDeleteSuccess(
            FileMessage message, String fromNode, String toNode
    ) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.DELETE)
                .replicationStatus(ReplicationStatus.OK)
                .toId(toNode)
                .fromId(fromNode)
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createDeleteFailed(
            FileMessage message, String fromNode, String toNode
    ) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.DELETE)
                .replicationStatus(ReplicationStatus.FAILED)
                .toId(toNode)
                .fromId(fromNode)
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }
}
