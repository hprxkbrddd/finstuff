package com.finstuff.repository.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finstuff.repository.dto.AccountEnlargedDTO;
import com.finstuff.repository.dto.NewAccountDTO;
import com.finstuff.repository.dto.TitleUpdateDTO;
import com.rabbitmq.client.Channel;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitAccountConsumerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Message message;

    @Mock
    private Channel channel;

    private RabbitAccountConsumer rabbitAccountConsumer;

    private final String accExchange = "test-exchange";
    private final String rkNewRes = "rk.new";
    private final String rkUpdTitleRes = "rk.title-upd";
    private final String rkDelRes = "rk.del";

    @BeforeEach
    void setUp() {
        rabbitAccountConsumer = new RabbitAccountConsumer(accountService, objectMapper);
        rabbitAccountConsumer.rabbitTemplate = rabbitTemplate;
        rabbitAccountConsumer.accExchange = accExchange;
        rabbitAccountConsumer.rkNewRes = rkNewRes;
        rabbitAccountConsumer.rkUpdTitleRes = rkUpdTitleRes;
        rabbitAccountConsumer.rkDelRes = rkDelRes;

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setDeliveryTag(1L); // Устанавливаем deliveryTag для ack
        when(message.getMessageProperties()).thenReturn(messageProperties);
    }

    @Test
    void add_ShouldProcessValidRequest() throws IOException, BadRequestException {
        // Arrange
        NewAccountDTO dto = new NewAccountDTO("Test Title", "user123");
        AccountEnlargedDTO expectedResponse = new AccountEnlargedDTO("acc123", "Test Title", "user123", null);

        when(accountService.addAccount(anyString(), anyString())).thenReturn(expectedResponse);

        // Act
        rabbitAccountConsumer.add(dto, message, channel);

        // Assert
        verify(accountService).addAccount("Test Title", "user123");
        verify(rabbitTemplate).convertAndSend(eq(accExchange), eq(rkNewRes), eq(expectedResponse));
    }

    @Test
    void add_ShouldHandleBlankTitle() throws IOException {
        // Arrange
        NewAccountDTO dto = new NewAccountDTO("", "user123");

        // Act
        rabbitAccountConsumer.add(dto, message, channel);

        // Assert
        verify(accountService, never()).addAccount(anyString(), anyString());
        verify(rabbitTemplate).convertAndSend(eq(accExchange), eq(rkDelRes), any(AccountEnlargedDTO.class));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void updateTitle_ShouldProcessValidRequest() throws IOException, BadRequestException {
        // Arrange
        TitleUpdateDTO dto = new TitleUpdateDTO("acc123", "New Title");
        AccountEnlargedDTO expectedResponse = new AccountEnlargedDTO("acc123", "New Title", "user123", null);

        when(accountService.updateTitle(anyString(), anyString())).thenReturn(expectedResponse);

        // Act
        rabbitAccountConsumer.updateTitle(dto, message, channel);

        // Assert
        verify(accountService).updateTitle("acc123", "New Title");
        verify(rabbitTemplate).convertAndSend(eq(accExchange), eq(rkUpdTitleRes), eq(expectedResponse));
    }

    @Test
    void updateTitle_ShouldHandleEntityNotFound() throws IOException, BadRequestException {
        // Arrange
        TitleUpdateDTO dto = new TitleUpdateDTO("acc123", "New Title");
        when(accountService.updateTitle(anyString(), anyString())).thenThrow(new EntityNotFoundException("Not found"));

        // Act
        rabbitAccountConsumer.updateTitle(dto, message, channel);

        // Assert
        verify(accountService).updateTitle("acc123", "New Title");
        verify(rabbitTemplate).convertAndSend(eq(accExchange), eq(rkUpdTitleRes), any(AccountEnlargedDTO.class));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void delete_ShouldProcessValidRequest() throws IOException, BadRequestException {
        // Arrange
        String accountId = "acc123";
        AccountEnlargedDTO expectedResponse = new AccountEnlargedDTO("acc123", "Test Title", "user123", null);

        when(accountService.deleteAccount(anyString())).thenReturn(expectedResponse);

        // Act
        rabbitAccountConsumer.delete(accountId, message, channel);

        // Assert
        verify(accountService).deleteAccount("acc123");
        verify(rabbitTemplate).convertAndSend(eq(accExchange), eq(rkDelRes), eq(expectedResponse));
    }

    @Test
    void delete_ShouldHandleBlankId() throws IOException {
        // Arrange
        String accountId = "";

        // Act
        rabbitAccountConsumer.delete(accountId, message, channel);

        // Assert
        verify(accountService, never()).deleteAccount(anyString());
        verify(rabbitTemplate).convertAndSend(eq(accExchange), eq(rkDelRes), any(AccountEnlargedDTO.class));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void handleNotFoundException_ShouldSendNullResponse() throws IOException {
        // Arrange
        String accountId = "acc123";
        String methodName = "TEST";
        String routingKey = "rk.test";
        EntityNotFoundException exception = new EntityNotFoundException("Not found");

        // Act
        rabbitAccountConsumer.handleNotFoundException(accountId, channel, message, exception, methodName, routingKey);

        // Assert
        verify(rabbitTemplate).convertAndSend(eq(accExchange), eq(routingKey), any(AccountEnlargedDTO.class));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void handleIllegalArgumentException_ShouldSendNullResponse() throws IOException {
        // Arrange
        String methodName = "TEST";
        String routingKey = "rk.test";
        BadRequestException exception = new BadRequestException("Bad request");

        // Act
        rabbitAccountConsumer.handleIllegalArgumentException(channel, message, exception, methodName, routingKey);

        // Assert
        verify(rabbitTemplate).convertAndSend(eq(accExchange), eq(routingKey), any(AccountEnlargedDTO.class));
        verify(channel).basicAck(1L, false);
    }
}