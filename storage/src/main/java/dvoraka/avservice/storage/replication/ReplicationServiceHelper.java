package dvoraka.avservice.storage.replication;

import dvoraka.avservice.common.data.Command;
import dvoraka.avservice.common.data.DefaultReplicationMessage;
import dvoraka.avservice.common.data.MessageRouting;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.common.data.ReplicationMessage;
import dvoraka.avservice.common.data.ReplicationStatus;

/**
 * Replication helper for service commands.
 */
public interface ReplicationServiceHelper {

    default ReplicationMessage createExistsRequest(String filename, String owner, String nodeId) {
        return new DefaultReplicationMessage.Builder(null)
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.BROADCAST)
                .command(Command.EXISTS)
                .fromId(nodeId)
                .filename(filename)
                .owner(owner)
                .build();
    }

    default ReplicationMessage createExistsReply(ReplicationMessage message, String nodeId) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.UNICAST)
                .command(Command.EXISTS)
                .replicationStatus(ReplicationStatus.OK)
                .fromId(nodeId)
                .toId(message.getFromId())
                .build();
    }

    default ReplicationMessage createStatusRequest(String filename, String owner) {
        return new DefaultReplicationMessage.Builder(null)
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.BROADCAST)
                .command(Command.STATUS)
                .filename(filename)
                .owner(owner)
                .build();
    }

    default ReplicationMessage createDiscoverRequest(String nodeId) {
        return new DefaultReplicationMessage.Builder(null)
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.BROADCAST)
                .command(Command.DISCOVER)
                .fromId(nodeId)
                .build();
    }

    default ReplicationMessage createDiscoverReply(ReplicationMessage message, String nodeId) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.UNICAST)
                .command(Command.DISCOVER)
                .replicationStatus(ReplicationStatus.READY)
                .fromId(nodeId)
                .toId(message.getFromId())
                .build();
    }

    default ReplicationMessage createLockRequest(
            String filename, String owner, String nodeId, long sequence) {
        return new DefaultReplicationMessage.Builder(null)
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.BROADCAST)
                .command(Command.LOCK)
                .fromId(nodeId)
                .sequence(sequence)
                .filename(filename)
                .owner(owner)
                .build();
    }

    default ReplicationMessage createLockSuccessReply(ReplicationMessage message, String nodeId) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.UNICAST)
                .command(Command.LOCK)
                .replicationStatus(ReplicationStatus.READY)
                .fromId(nodeId)
                .toId(message.getFromId())
                .sequence(message.getSequence())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createLockFailReply(ReplicationMessage message, String nodeId) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.UNICAST)
                .command(Command.LOCK)
                .replicationStatus(ReplicationStatus.FAILED)
                .fromId(nodeId)
                .toId(message.getFromId())
                .sequence(message.getSequence())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createUnLockRequest(
            String filename, String owner, String nodeId, long sequence) {
        return new DefaultReplicationMessage.Builder(null)
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.BROADCAST)
                .command(Command.UNLOCK)
                .fromId(nodeId)
                .sequence(sequence)
                .filename(filename)
                .owner(owner)
                .build();
    }

    default ReplicationMessage createUnlockSuccessReply(
            ReplicationMessage message, String nodeId) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.UNICAST)
                .command(Command.UNLOCK)
                .replicationStatus(ReplicationStatus.OK)
                .fromId(nodeId)
                .toId(message.getFromId())
                .filename(message.getFilename())
                .owner(message.getOwner())
                .build();
    }

    default ReplicationMessage createSequenceRequest(String nodeId) {
        return new DefaultReplicationMessage.Builder(null)
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.BROADCAST)
                .command(Command.SEQUENCE)
                .fromId(nodeId)
                .build();
    }

    default ReplicationMessage createSequenceReply(
            ReplicationMessage message, String nodeId, long sequence
    ) {
        return new DefaultReplicationMessage.Builder(null)
                .correlationId(message.getId())
                .type(MessageType.REPLICATION_SERVICE)
                .routing(MessageRouting.UNICAST)
                .command(Command.SEQUENCE)
                .fromId(nodeId)
                .toId(message.getFromId())
                .sequence(sequence)
                .build();
    }
}
