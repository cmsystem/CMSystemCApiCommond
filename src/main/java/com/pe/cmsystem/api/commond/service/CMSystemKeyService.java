package com.pe.cmsystem.api.commond.service;

import javax.crypto.SecretKey;

public interface CMSystemKeyService {
    SecretKey deriveKeyFromClient() throws Exception;
}
