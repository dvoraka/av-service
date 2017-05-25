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

    default ReplicationMessage createSaveMessage(
            FileMessage message, String nodeId, String neighbourId
    ) {
        return new DefaultReplicationMessage.Builder(null)
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.SAVE)
                .toId(neighbourId)
                .fromId(nodeId)
                .data(message.getData())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createSaveSuccess(ReplicationMessage message, String nodeId) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.SAVE)
                .replicationStatus(ReplicationStatus.OK)
                .toId(message.getFromId())
                .fromId(nodeId)
                .data(message.getData())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createSaveFailed(ReplicationMessage message, String nodeId) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.SAVE)
                .replicationStatus(ReplicationStatus.FAILED)
                .toId(message.getFromId())
                .fromId(nodeId)
                .data(message.getData())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createLoadMessage(
            FileMessage message, String nodeId, String neighbourId
    ) {
        return new DefaultReplicationMessage.Builder(null)
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.LOAD)
                .toId(neighbourId)
                .fromId(nodeId)
                .data(message.getData())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createLoadReply(
            FileMessage message, String toNodeId, String fromNodeId
    ) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getCorrelationId())
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.LOAD)
                .replicationStatus(ReplicationStatus.OK)
                .toId(fromNodeId)
                .fromId(toNodeId)
                .data(message.getData())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createUpdateMessage(
            FileMessage message, String nodeId, String neighbourId
    ) {
        return new DefaultReplicationMessage.Builder(null)
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.UPDATE)
                .toId(neighbourId)
                .fromId(nodeId)
                .data(message.getData())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createDeleteMessage(
            FileMessage message, String nodeId, String neighbourId
    ) {
        return new DefaultReplicationMessage.Builder(null)
                .type(MessageType.REPLICATION_COMMAND)
                .routing(MessageRouting.UNICAST)
                .command(Command.DELETE)
                .toId(neighbourId)
                .fromId(nodeId)
                .data(message.getData())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }
}
