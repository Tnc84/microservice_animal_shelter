package org.tnc.pethotelmicroservice.controler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tnc.pethotelmicroservice.controler.dtoMapper.DtoMapper;
import org.tnc.pethotelmicroservice.controler.records.BookingDTO;
import org.tnc.pethotelmicroservice.service.serviceInterfaces.BookingServiceInterface;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final DtoMapper dtoMapper;
    private final BookingServiceInterface  bookingService;

    // TODO: Add role-based filtering (filtered by user role)
    @GetMapping("/getAll")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        var bookings = dtoMapper.listBookingDomainToBookingDto(bookingService.getAllBookings());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        var bookingDomain = bookingService.getBookingById(id);
        if (bookingDomain == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dtoMapper.bookingDomainToBookingDto(bookingDomain));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByUserId(@PathVariable Long userId) {
        var bookings = dtoMapper.listBookingDomainToBookingDto(bookingService.getBookingsByUserId(userId));
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByRoomId(@PathVariable Long roomId) {
        var bookings = dtoMapper.listBookingDomainToBookingDto(bookingService.getBookingsByRoomId(roomId));
        return ResponseEntity.ok(bookings);
    }

    // TODO: Add @PreAuthorize("hasRole('USER')") or similar security annotation
    @PostMapping
    public ResponseEntity<BookingDTO> addBooking(@RequestBody BookingDTO bookingDTO) {
        var bookingDomain = bookingService.addBooking(dtoMapper.bookingDtoToBookingDomain(bookingDTO));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dtoMapper.bookingDomainToBookingDto(bookingDomain));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO) {
        var bookingDomain = bookingService.updateBooking(id, dtoMapper.bookingDtoToBookingDomain(bookingDTO));
        if (bookingDomain == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dtoMapper.bookingDomainToBookingDto(bookingDomain));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        var bookingDomain = bookingService.getBookingById(id);
        if (bookingDomain == null) {
            return ResponseEntity.notFound().build();
        }
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<BookingDTO> confirmBooking(@PathVariable Long id) {
        var bookingDomain = bookingService.confirmBooking(id);
        if (bookingDomain == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dtoMapper.bookingDomainToBookingDto(bookingDomain));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<BookingDTO> completeBooking(@PathVariable Long id) {
        var bookingDomain = bookingService.completeBooking(id);
        if (bookingDomain == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dtoMapper.bookingDomainToBookingDto(bookingDomain));
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<String, Object>> checkRoomAvailability(
            @RequestParam Long roomId,
            @RequestParam LocalDateTime checkIn,
            @RequestParam LocalDateTime checkOut) {
        boolean isAvailable = bookingService.checkRoomAvailability(roomId, checkIn, checkOut);
        Map<String, Object> response = new HashMap<>();
        response.put("roomId", roomId);
        response.put("checkIn", checkIn);
        response.put("checkOut", checkOut);
        response.put("available", isAvailable);
        return ResponseEntity.ok(response);
    }
}
