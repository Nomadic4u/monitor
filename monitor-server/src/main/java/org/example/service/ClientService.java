package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Client;
import org.example.entity.vo.request.ClientDetailVo;
import org.example.entity.vo.request.RenameClientVo;
import org.example.entity.vo.request.RuntimeDetailVo;
import org.example.entity.vo.response.ClientPreviewVo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ClientService extends IService<Client> {

    boolean verifyAndRegister(String token);

    Client findClientById(int id);

    Client findClientByToken(String token);

    String registerTOken();

    void updateClientDetails(ClientDetailVo vo, Client client);

    void updateRuntimeDetails(RuntimeDetailVo vo, Client client);

    List<ClientPreviewVo> listAllClient();

    void renameClient(RenameClientVo vo);
}
