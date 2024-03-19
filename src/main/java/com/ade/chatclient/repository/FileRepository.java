package com.ade.chatclient.repository;

import java.util.concurrent.CompletableFuture;

public interface FileRepository {
    CompletableFuture<byte[]> getFile(String fileId);

    void clear();
}
