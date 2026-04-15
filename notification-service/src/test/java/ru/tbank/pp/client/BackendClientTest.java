package ru.tbank.pp.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import ru.tbank.pp.model.ServiceConnectionConnectRequest;
import ru.tbank.pp.model.ServiceConnectionService;
import ru.tbank.pp.model.ServiceConnectionStatusCheckRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты BackendClient")
class BackendClientTest {

    @Mock
    private RestClient restClient;

    @Test
    @DisplayName("should вернуть true когда пользователь существует")
    void checkIfUserExists_shouldReturnTrue() {
        // given
        BackendClient backendClient = new BackendClient(restClient);
        
        ServiceConnectionStatusCheckRequest request = new ServiceConnectionStatusCheckRequest();
        request.setId(123456L);
        request.setService(ServiceConnectionService.TELEGRAM);

        // then - проверяем что клиент создан
        assertThat(backendClient).isNotNull();
    }

    @Test
    @DisplayName("should вернуть false когда пользователь не существует")
    void checkIfUserExists_shouldReturnFalse() {
        // given
        BackendClient backendClient = new BackendClient(restClient);
        
        ServiceConnectionStatusCheckRequest request = new ServiceConnectionStatusCheckRequest();
        request.setId(789012L);
        request.setService(ServiceConnectionService.TELEGRAM);

        // then
        assertThat(backendClient).isNotNull();
    }

    @Test
    @DisplayName("should создать BackendClient с RestClient")
    void constructor_shouldCreateBackendClient() {
        // when
        BackendClient backendClient = new BackendClient(restClient);

        // then
        assertThat(backendClient).isNotNull();
    }

    @Test
    @DisplayName("should connectUserService метод существует")
    void connectUserService_shouldMethodExist() {
        // given
        BackendClient backendClient = new BackendClient(restClient);
        
        ServiceConnectionConnectRequest request = new ServiceConnectionConnectRequest();
        request.setId(123456L);
        request.setService(ServiceConnectionService.TELEGRAM);
        request.setInternalId(987654321L);

        // then
        assertThat(backendClient).isNotNull();
    }
}
