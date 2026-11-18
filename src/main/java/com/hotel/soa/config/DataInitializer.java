package com.hotel.soa.config;

import com.hotel.soa.authentication.entity.User;
import com.hotel.soa.authentication.entity.User.UserRole;
import com.hotel.soa.authentication.repository.UserRepository;
import com.hotel.soa.customer.entity.Customer;
import com.hotel.soa.customer.repository.CustomerRepository;
import com.hotel.soa.housekeeping.entity.Housekeeping;
import com.hotel.soa.housekeeping.entity.Housekeeping.CleaningStatus;
import com.hotel.soa.housekeeping.repository.HousekeepingRepository;
import com.hotel.soa.room.entity.Room;
import com.hotel.soa.room.entity.Room.RoomStatus;
import com.hotel.soa.room.entity.RoomType;
import com.hotel.soa.room.repository.RoomRepository;
import com.hotel.soa.room.repository.RoomTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final HousekeepingRepository housekeepingRepository;

    public DataInitializer(UserRepository userRepository,
                           CustomerRepository customerRepository,
                           RoomTypeRepository roomTypeRepository,
                           RoomRepository roomRepository,
                           HousekeepingRepository housekeepingRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.housekeepingRepository = housekeepingRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Initialize Default Users
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password("admin123")
                    .email("admin@grandhotel.com")
                    .role(UserRole.ADMIN)
                    .build());

            userRepository.save(User.builder()
                    .username("receptionist")
                    .password("recep123")
                    .email("reception@grandhotel.com")
                    .role(UserRole.RECEPTIONIST)
                    .build());

            userRepository.save(User.builder()
                    .username("customer")
                    .password("cust123")
                    .email("rahul@gmail.com")
                    .role(UserRole.CUSTOMER)
                    .build());
        }

        // Initialize Default Customers
        if (customerRepository.count() == 0) {
            customerRepository.save(Customer.builder()
                    .name("Rahul Sharma")
                    .email("rahul@gmail.com")
                    .phone("9876543210")
                    .address("Chennai, Tamil Nadu")
                    .build());

            customerRepository.save(Customer.builder()
                    .name("Anita Patel")
                    .email("anita.patel@example.com")
                    .phone("9823456789")
                    .address("Mumbai, Maharashtra")
                    .build());
        }

        // Initialize Room Types
        if (roomTypeRepository.count() == 0) {
            RoomType single = roomTypeRepository.save(RoomType.builder()
                    .name("SINGLE")
                    .description("Cozy single room with high-speed Wi-Fi, queen bed, and modern bathroom.")
                    .basePrice(BigDecimal.valueOf(1500.00))
                    .build());

            RoomType dbl = roomTypeRepository.save(RoomType.builder()
                    .name("DOUBLE")
                    .description("Spacious room with king bed, workspace, city view, and luxury amenities.")
                    .basePrice(BigDecimal.valueOf(2500.00))
                    .build());

            RoomType deluxe = roomTypeRepository.save(RoomType.builder()
                    .name("DELUXE")
                    .description("Deluxe suite with private balcony, premium entertainment, and breakfast.")
                    .basePrice(BigDecimal.valueOf(4000.00))
                    .build());

            RoomType suite = roomTypeRepository.save(RoomType.builder()
                    .name("SUITE")
                    .description("Executive luxury suite with separate living room, jacuzzi, and butler service.")
                    .basePrice(BigDecimal.valueOf(7500.00))
                    .build());

            // Initialize 10 Rooms
            createRoom("101", dbl, BigDecimal.valueOf(2500.00), 2, 1);
            createRoom("102", dbl, BigDecimal.valueOf(2500.00), 2, 1);
            createRoom("103", single, BigDecimal.valueOf(1500.00), 1, 1);
            createRoom("104", single, BigDecimal.valueOf(1500.00), 1, 1);

            createRoom("201", deluxe, BigDecimal.valueOf(4000.00), 3, 2);
            createRoom("202", deluxe, BigDecimal.valueOf(4000.00), 3, 2);
            createRoom("203", dbl, BigDecimal.valueOf(2600.00), 2, 2);

            createRoom("301", suite, BigDecimal.valueOf(7500.00), 4, 3);
            createRoom("302", suite, BigDecimal.valueOf(7500.00), 4, 3);
            createRoom("303", deluxe, BigDecimal.valueOf(4200.00), 3, 3);
        }
    }

    private void createRoom(String roomNo, RoomType type, BigDecimal price, int capacity, int floor) {
        Room room = roomRepository.save(Room.builder()
                .roomNumber(roomNo)
                .roomType(type)
                .price(price)
                .capacity(capacity)
                .floor(floor)
                .status(RoomStatus.AVAILABLE)
                .build());

        housekeepingRepository.save(Housekeeping.builder()
                .room(room)
                .status(CleaningStatus.CLEAN)
                .lastCleaned(LocalDateTime.now())
                .assignedTo("Housekeeping Staff")
                .notes("Initial inspection passed. Ready for guests.")
                .build());
    }
}
