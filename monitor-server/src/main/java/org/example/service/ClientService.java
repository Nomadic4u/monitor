package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Client;
import org.example.entity.vo.request.ClientDetailVo;

public interface ClientService extends IService<Client> {

    boolean verifyAndRegister(String token);

    Client findClientById(int id);

    Client findClientByToken(String token);

    String registerTOken();

    void updateClientDetails(ClientDetailVo vo, Client client);
}