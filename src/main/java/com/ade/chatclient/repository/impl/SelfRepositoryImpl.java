package com.ade.chatclient.repository.impl;

import com.ade.chatclient.api.FileApi;
import com.ade.chatclient.api.SelfApi;
import com.ade.chatclient.domain.Company;
import com.ade.chatclient.domain.User;
import com.ade.chatclient.dtos.AuthResponse;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import com.ade.chatclient.repository.SelfRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class SelfRepositoryImpl implements SelfRepository {
    private final SelfApi selfApi;
    private final FileApi fileApi;

    @Setter
    @Getter
    private User myself;

    @Setter
    @Getter
    private Company company;

    @Override
    public CompletableFuture<AuthResponse> changePassword(ChangePasswordRequest request) {
        return selfApi.changePassword(request);
    }

    @Override
    public CompletableFuture<User> changeUserInfo(User request) {
        return selfApi.changeUserInfo(request);
    }

    @Override
    public void clear() {
        myself = null;
        company = null;
    }

    @Override
    public CompletableFuture<User> changeProfilePhoto(Path path) {
        return fileApi.uploadUserPhoto(myself.getId(), path).thenApply(user -> {
            myself = user;
            return user;
        });
    }
}
