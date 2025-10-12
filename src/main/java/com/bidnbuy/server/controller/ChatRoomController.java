package com.bidnbuy.server.controller;

import com.bidnbuy.server.dto.ChatMessageDto;
import com.bidnbuy.server.dto.ChatRoomCreateRequestDto;
import com.bidnbuy.server.dto.ChatRoomDto;
import com.bidnbuy.server.service.ChatMessageService;
import com.bidnbuy.server.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/chatrooms")
@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/{auctionId}")
    public ResponseEntity<ChatRoomDto> createChatRoom(@PathVariable Long auctionId,
                                                      @AuthenticationPrincipal Long userId,
                                                      @RequestBody ChatRoomCreateRequestDto requestDto){
        requestDto.setAuctionId(auctionId);
        requestDto.setBuyerId(userId);
        try{
            ChatRoomDto chatRoom = chatRoomService.findOrCreateChatRoom(requestDto);
                    return ResponseEntity.ok(chatRoom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (Exception e){
            throw new RuntimeException("채팅방 생성 중 오류 발생",e );
        }
    }

    //메세지 조회
    @GetMapping("/{chatroomId}/message")
    public ResponseEntity<List<ChatMessageDto>> getChatMessages(
            @PathVariable("chatroomId") Long chatroomId){
        List<ChatMessageDto> messages = chatMessageService.getMessageByChatRoomId(chatroomId);

        return ResponseEntity.ok(messages);
    }
}
