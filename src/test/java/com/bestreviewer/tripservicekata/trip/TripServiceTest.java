package com.bestreviewer.tripservicekata.trip;

import com.bestreviewer.tripservicekata.exception.UserNotLoggedInException;
import com.bestreviewer.tripservicekata.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static com.bestreviewer.tripservicekata.trip.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

public class TripServiceTest {
    private static final User NOT_LOGGED_USER = null;
    private static final User LOGGED_USER = new User();
    private static final User ANOTHER_USER = new User();
    private static final User GUEST = new User();
    private static final Trip TO_JEJU = new Trip();
    private static final Trip TO_BUSAN = new Trip();

    @Mock
    TripDAO tripDAO;

    @InjectMocks
    @Spy private TripService tripService = new TripService();

    @Test
    void test_fail() {
        //fail();
    }

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should throw on exception when user is not logged.")
    public void testThrowExceptionWhenNotLogged(){
        Assertions.assertThrows(Exception.class,()-> tripService.getTripsByUser(NOT_LOGGED_USER,GUEST));
    }

    @Test
    @DisplayName("친구가 아닌 경우 trip 을 반환하지 않는다.")
    public void testReturnNoTripWhenUserNotFriend(){
        User friend = aUser()
                .friendsWith(ANOTHER_USER)
                .withTrips(TO_JEJU)
                .build();

        List<Trip> friendTrips = tripService.getTripsByUser(friend,LOGGED_USER );
        assertEquals(0,friendTrips.size());
    }

    @Test
    @DisplayName("친구인 경우 trip 을 반환한다.")
    public void testReturnTripWhenUserAreFriend(){
        User friend = aUser()
                .friendsWith(ANOTHER_USER, LOGGED_USER)
                .withTrips(TO_JEJU,TO_BUSAN)
                .build();

        when(tripDAO.tripsBy(friend)).thenReturn(friend.trips());
        List<Trip> friendTrips = tripService.getTripsByUser(friend, LOGGED_USER);
        assertEquals(2,friendTrips.size());
    }

    private class TestableTripService extends TripService{
        @Override
        protected List<Trip> tripsBy(User user) throws UserNotLoggedInException {
            return user.trips();
        }

    }
}

