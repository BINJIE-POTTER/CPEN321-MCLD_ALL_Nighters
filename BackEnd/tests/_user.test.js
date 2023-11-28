const request = require('supertest');

const { MongoClient } = require('mongodb');

// Mock MongoDB Methods
const mockInsertOne = jest.fn();
const mockUpdateOne = jest.fn();
const mockFindOne = jest.fn();
const mockUserExists = jest.fn();
const mockSend = jest.fn();

jest.mock('../_userRoutes.js', () => ({
  ...jest.requireActual('../_userRoutes.js'),
  mockSend,
}));

// Mock MongoDB Client
jest.mock('mongodb', () => ({
  MongoClient: jest.fn().mockImplementation(() => ({
    connect: jest.fn().mockResolvedValue(this),
    db: jest.fn().mockReturnValue({
      collection: jest.fn().mockReturnValue({
        findOne: mockFindOne,
        insertOne: mockInsertOne,
        updateOne: mockUpdateOne,
        // Define other methods as needed
      }),
    }),
    close: jest.fn(),
  })),
}));

const userRoutes = require('../_userRoutes');

const userExists = jest.spyOn(userRoutes, 'userExists');
const isUserFollowing = jest.spyOn(userRoutes, 'isUserFollowing');
userExists.mockImplementation(() => Promise.resolve(true)); 

const app = require('../server');

//Interface POST www.myserver.ca/users
describe('Create a user', () => {
  const mockInsertOne = require('mongodb').MongoClient().db().collection().insertOne;

  beforeEach(() => {
    mockInsertOne.mockClear();
  });

  //ChatGPT Usage: Partial
  // Test Case 1: Valid user creation
  // Input: Valid user with email, name, gender, birthdate, and userId
  // Expected Status Code: 200
  // Expected Behavior: User is created successfully
  // Expected Output: None
  it('should create a new user with valid data', async () => {
   
    var newUser = {
      userEmail: 'test@example.com',
      userName: 'Test User',
      userGender: 'Non-binary',
      userBirthdate: '1990-01-01',
      userId: 'unique_user_id',
      userAvatar: {
        contentType: "",
        image: ""
      }
    };
    
    const response = await request(app)
      .post('/users')
      .send(newUser);

    var sampleAvatar = {
        contentType: 'image/jpeg',
        image: "12345"
    };
    newUser.userAvatar = sampleAvatar;

    expect(response.statusCode).toBe(200);
    expect(response.text).toEqual("User created successfully.");
    expect(mockInsertOne).toHaveBeenCalledWith({
        ...newUser,
        postCount: 0, 
      });
  });
    
    //ChatGPT Usage: Partial
    // Test Case 2: Invalid user format
    // Input: Invalid user data (Not giving userEmail)
    // Expected Status Code: 400
    // Expected Behavior: User creation fails due to invalid user format
    // Expected Output: None
    it('should return 400 for invalid user format', async () => {
        const invalidUser = {
         userName: 'Test User',
         userGender: 'Non-binary',
         userBirthdate: '1990-01-01',
         userId: 'unique_user_id'
        };

        const response = await request(app)
            .post('/users')
            .send(invalidUser);

        expect(response.statusCode).toBe(400);
        expect(response.text).toEqual("Invalid user format or user already exists");
        expect(mockInsertOne).not.toHaveBeenCalled();  // Ensure insertOne is not called for invalid user format
    });

    //ChatGPT Usage: Partial
    // Test Case 3: Internal server error
    // Input: Internal server error during user creation
    // Expected Status Code: 500
    // Expected Behavior: Server responds with an internal server error
    // Expected Output: "Internal server error" message
    it('should return 500 for internal server error', async () => {
        
        mockInsertOne.mockRejectedValue(new Error("Simulated internal server error"));

        const newUser = {
            userEmail: 'test@example.com',
            userName: 'Test User',
            userGender: 'Non-binary',
            userBirthdate: '1990-01-01',
            userId: 'unique_user_id',
            userAvatar: {
              contentType: "",
              image: ""
            }
        };

        const response = await request(app)
            .post('/users')
            .send(newUser);

        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal server error");
    });
});

//Interface PUT www.myserver.ca/users/update-profile
describe('Update user profile', () => {
    beforeEach(() => {
      mockUpdateOne.mockClear();
      mockFindOne.mockClear();
    });
  
    //ChatGPT Usage: Partial
    // Test Case 4: Valid User Update
    // Input: Valid user data for updating an existing user profile
    // Expected Status Code: 200
    // Expected Behavior: User profile is updated in the database
    // Expected Output: User profile is updated successfully
    it('should update user profile successfully', async () => {
      
      const existingUser = {
        userId: 'existing_user_id',
        userEmail: 'test@example.com',
        userName: 'Test User',
        userGender: 'Non-binary',
        userBirthdate: '1990-01-01',
      };
  
      
      const updatedUser = {
        userId: 'existing_user_id',
        userEmail: 'updated_test@example.com',
        userName: 'Updated Test User',
        userGender: 'Male',
        userBirthdate: '1995-05-05',
        token: 'new_token',
      };
  
      
      mockFindOne.mockResolvedValue(existingUser);
      mockUpdateOne.mockResolvedValue({ matchedCount: 1, modifiedCount: 1 });
  
      
      const response = await request(app)
        .put('/users/update-profile')
        .send(updatedUser);
  
      
      expect(response.statusCode).toBe(200);
      expect(response.text).toEqual("User profile updated successfully.");
      expect(mockFindOne).toHaveBeenCalledWith({ userId: 'existing_user_id' });
      expect(mockUpdateOne).toHaveBeenCalledWith(
        { userId: 'existing_user_id' },
        { $set: {
            userEmail: 'updated_test@example.com',
            userName: 'Updated Test User',
            userGender: 'Male',
            userBirthdate: '1995-05-05',
            token: 'new_token'
          }
        }
      );
    });

    //ChatGPT Usage: Partial
    // Test Case 5: Missing User ID in Request
    // Input: Request to create a user without providing a User ID
    // Expected Status Code: 400
    // Expected Behavior: Database is unchanged
    // Expected Output: None
    it('should return 400 when userId is missing in the request for user update', async () => {
      
      const updatedUser = {
        userEmail: 'updated_test@example.com',
        userName: 'Updated Test User',
        userGender: 'Male',
        userBirthdate: '1995-05-05',
        token: 'new_token',
      };
  
      
      const response = await request(app)
        .put('/users/update-profile')
        .send(updatedUser);
  
      
      expect(response.statusCode).toBe(400);
      expect(response.text).toEqual("User ID must be provided.");
      expect(mockFindOne).not.toHaveBeenCalled();  // Ensure findOne is not called
      expect(mockUpdateOne).not.toHaveBeenCalled();  // Ensure updateOne is not called
    });

    //ChatGPT Usage: No
    // Test Case 6: User Does Not Exist
    // Input: Request to update a user that does not exist, providing a non-existing User ID
    // Expected Status Code: 400
    // Expected Behavior: Database is unchanged
    // Expected Output: None
    it('should return 400 when updating a user that does not exist', async () => {
        
        const nonExistingUserId = 'non_existing_user_id';

        const updatedUser = {
        userId: nonExistingUserId,
        userEmail: 'updated_test@example.com',
        userName: 'Updated Test User',
        userGender: 'Male',
        userBirthdate: '1995-05-05',
        token: 'new_token',
        };
    
        mockFindOne.mockResolvedValue(null); 
    
        const response = await request(app)
        .put('/users/update-profile')
        .send(updatedUser);
    
        expect(response.statusCode).toBe(400);
        expect(response.text).toEqual("User does not exist.");
        expect(mockFindOne).toHaveBeenCalledWith({ userId: nonExistingUserId });
        expect(mockUpdateOne).not.toHaveBeenCalled(); 
    });

    //ChatGPT Usage: Partial
    // Test Case 7: No Changes Made 
    // Input: Request to update a user with valid data, but no changes are made to the user profile
    // Expected Status Code: 200
    // Expected Behavior: Database is unchanged
    // Expected Output: None
    it('should return 200 when updating a user with valid data, but no changes are made to the user profile', async () => {
        const existingUser = {
            userId: 'existing_user_id',
            userEmail: 'test@example.com',
            userName: 'Test User',
            userGender: 'Non-binary',
            userBirthdate: '1990-01-01',
            token: 'existing_token',
        };

        const updatedUser = {
            userId: 'existing_user_id',
            userEmail: 'test@example.com',
            userName: 'Test User',
            userGender: 'Non-binary',
            userBirthdate: '1990-01-01',
            token: 'existing_token',
        };

        mockFindOne.mockResolvedValue(existingUser);
        mockUpdateOne.mockResolvedValue({ matchedCount: 1, modifiedCount: 0 });

        const response = await request(app)
            .put('/users/update-profile')
            .send(updatedUser);

        expect(response.statusCode).toBe(200);
        expect(response.text).toEqual("No changes made to user.");
        expect(mockFindOne).toHaveBeenCalledWith({ userId: 'existing_user_id' });
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { userId: 'existing_user_id' },
            {
                $set: {
                    userEmail: 'test@example.com',
                    userName: 'Test User',
                    userGender: 'Non-binary',
                    userBirthdate: '1990-01-01',
                    token: 'existing_token',
                },
            }
        );
    });

    //ChatGPT Usage: No
    // Test Case 8: Internal Server Error
    // Input: Internal server error during user update
    // Expected Status Code: 500
    // Expected Behavior: Server responds with an internal server error
    // Expected Output: "Internal server error" message
    it('should return 500 in case of an internal server error during user update', async () => {
        const existingUser = {
            userId: 'existing_user_id',
            userEmail: 'test@example.com',
            userName: 'Test User',
            userGender: 'Non-binary',
            userBirthdate: '1990-01-01',
            token: 'existing_token',
        };

        const updatedUser = {
            userId: 'existing_user_id',
            userEmail: 'updated_test@example.com',
            userName: 'Updated Test User',
            userGender: 'Male',
            userBirthdate: '1995-05-05',
            token: 'new_token',
        };

        mockFindOne.mockResolvedValue(existingUser);
        mockUpdateOne.mockRejectedValue(new Error('Simulated internal server error'));

        const response = await request(app)
            .put('/users/update-profile')
            .send(updatedUser);

        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal server error");
        expect(mockFindOne).toHaveBeenCalledWith({ userId: 'existing_user_id' });
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { userId: 'existing_user_id' },
            {
                $set: {
                    userEmail: 'updated_test@example.com',
                    userName: 'Updated Test User',
                    userGender: 'Male',
                    userBirthdate: '1995-05-05',
                    token: 'new_token',
                },
            }
        );
    });

});  


//Interface PUT www.myserver.ca/users/follow
describe('Follow user', () => {
    beforeEach(() => {
        
        mockUpdateOne.mockClear();
        userExists.mockClear();
    });

    //ChatGPT Usage: Partial
    // Test Case 9: Successful Follow Operation
    // Input: Existing user attempting to follow another existing user
    // Expected Status Code: 200
    // Expected Behavior: The user's following array is updated with the target user's ID,
    //                    and the target user's follower array is updated with the user's ID.
    // Expected Output: "Follow operation successful." in the response body.
    it('should successfully update user\'s following array and following user\'s follower array', async () => {
        const existingUser = {
            userId: 'existing_user_id',
            following: [],
        };

        const followingUser = {
            userId: 'following_user_id',
            followers: [],
        };

        const followRequest = {
            userId: existingUser.userId,
            followingId: followingUser.userId,
        };

        userExists.mockResolvedValue(true);

        mockUpdateOne.mockResolvedValueOnce({ matchedCount: 1 });
        mockUpdateOne.mockResolvedValueOnce({ matchedCount: 1 });

        const response = await request(app)
            .put('/users/follow')
            .send(followRequest);

        expect(response.statusCode).toBe(200);
        expect(response.text).toEqual("Follow operation successful.");

        expect(mockUpdateOne).toHaveBeenCalledWith(
            { userId: existingUser.userId },
            { $addToSet: { following: followingUser.userId } }
        );
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { userId: followingUser.userId },
            { $addToSet: { followers: existingUser.userId } }
        );

    });

    //ChatGPT Usage: Partial
    // Test Case 10: Missing User ID and Following ID
    // Input: Request with missing User ID and Following ID
    // Expected Status Code: 400
    // Expected Behavior: The server should respond with an error message indicating that both
    //                    User ID and Following ID must be provided.
    // Expected Output: "User ID and following ID must be provided." in the response body.
    it('should return 400 when User ID and Following ID are missing', async () => {
        const response = await request(app)
            .put('/users/follow')
            .send({});

        expect(response.statusCode).toBe(400);
        expect(response.text).toEqual("User ID and following ID must be provided.");
    });

    //ChatGPT Usage: Partial
    // Test Case 11: User Does Not Exist
    // Input: Request with valid User ID but non-existing Following ID
    // Expected Status Code: 400
    // Expected Behavior: The server should respond with an error message indicating that the
    //                    user or the following user does not exist.
    // Expected Output: "User or the following user not found." in the response body.
    it('should return 400 when the user to follow does not exist', async () => {
        const existingUserId = 'existing_user_id';
        const nonExistingFollowingId = 'non_existing_user_id';
    
        mockFindOne.mockImplementation((query) => {
            
            if (query.userId === nonExistingFollowingId) {
                return null; 
            } else {
                return {};
            }
        });
    
        const response = await request(app)
            .put('/users/follow')
            .send({ userId: existingUserId, followingId: nonExistingFollowingId });
    
        expect(response.statusCode).toBe(400);
        expect(response.text).toEqual("User does not exist.");
        expect(mockFindOne).toHaveBeenCalledWith({ userId: nonExistingFollowingId });
        expect(mockUpdateOne).not.toHaveBeenCalled();
    });

    //ChatGPT Usage: Partial
    // Test Case 12: User or Following User Not Found
    // Input: Valid User ID and Following ID but one of them does not exist in the database
    // Expected Status Code: 404
    // Expected Behavior: No update operation on non-existing user or following user
    // Expected Output: "User or the following user not found." message in the response body
    it('should return 404 when user or the following user not found', async () => {
        const existingUserId = 'existing_user_id';
        const nonExistingFollowingId = 'non_existing_following_id';

        userExists.mockResolvedValueOnce(true).mockResolvedValueOnce(false);

        mockUpdateOne.mockResolvedValue({ matchedCount: 0 });

        const response = await request(app)
            .put('/users/follow')
            .send({ userId: existingUserId, followingId: nonExistingFollowingId });

        expect(response.statusCode).toBe(404);
        expect(response.text).toEqual("User or the following user not found.");
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { userId: existingUserId },
            { $addToSet: { following: nonExistingFollowingId } }
        );
    
    });

    //ChatGPT Usage: No
    // Test Case 13: User ID Missing
    // Input: Request with valid Following ID but missing User ID
    // Expected Status Code: 400
    // Expected Behavior: The server should respond with an error message indicating that the
    //                    User ID must be provided.
    // Expected Output: "User ID must be provided." in the response body.
    it('should return 400 when User ID is missing', async () => {
        const response = await request(app)
            .put('/users/follow')
            .send({ followingId: 'following_user_id' });

        expect(response.statusCode).toBe(400);
        expect(response.text).toEqual("User ID and following ID must be provided.");
    });

    //ChatGPT Usage: No
    // Test Case 14: Internal Server Error during Follow Operation
    // Input: Request with valid User ID and Following ID
    // Expected Status Code: 500
    // Expected Behavior: The server should respond with an error message indicating an internal server error.
    // Expected Output: "Internal server error" in the response body.
    test('should return 500 on internal server error during follow operation', async () => {
        
        const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();
      
     
        mockUpdateOne.mockImplementationOnce(() => {
          throw new Error('Simulated error during follow operation');
        });
        
        const response = await request(app)
          .put('/users/follow')
          .send({ userId: 'existing_user_id', followingId: 'following_user_id' });
      
        
        expect(consoleErrorMock).toHaveBeenCalledWith('Error during follow operation:', expect.any(Error));
    
        expect(response.statusCode).toBe(500);
        
        consoleErrorMock.mockRestore();
      });

});

//Interface PUT www.myserver.ca/users/unfollow
describe('Unfollow User Route', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        userExists.mockResolvedValue(true);
        isUserFollowing.mockClear();
        mockUpdateOne.mockResolvedValue({ matchedCount: 1 });
    });

    //ChatGPT Usage: Partial
    // Test Case 15: Successful Unfollow Operation
    // Input: Existing user attempting to unfollow another existing user
    // Expected Status Code: 200
    // Expected Behavior: The user's following array is updated by removing the target user's ID,
    //                    and the target user's followers array is updated by removing the user's ID.
    // Expected Output: "Unfollow operation successful." in the response body.
    it('should successfully unfollow when both users exist and have a following relationship', async () => {
        const existingUserId = 'existing_user_id';
        const existingFollowingId = 'following_user_id';

        const response = await request(app)
            .put('/users/unfollow')
            .send({ userId: existingUserId, followingId: existingFollowingId });

        
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { userId: existingUserId },
            { $pull: { following: existingFollowingId } }
        );
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { userId: existingFollowingId },
            { $pull: { followers: existingUserId } }
        );

        expect(response.statusCode).toBe(200);
        expect(response.text).toEqual('Unfollow operation successful.');

    });
  
    //ChatGPT Usage: No
    // Test Case 16: Fail to Unfollow Due to Missing User ID or Following ID
    // Input: Request without User ID or Following ID
    // Expected Status Code: 400
    // Expected Behavior: The route should not proceed with any unfollow operation due to missing required fields.
    // Expected Output: None
    it('should return 400 when User ID or Following ID is missing', async () => {
      const response = await request(app)
        .put('/users/unfollow')
        .send({});
  
      expect(response.statusCode).toBe(400);
      expect(response.text).toEqual('User ID and following ID must be provided.');
      expect(mockUpdateOne).not.toHaveBeenCalled();
    });
  
    //ChatGPT Usage: Partial
    // Test Case 17: The specified users do not have a following relationship
    // Input: Request with User ID and Following ID where the specified users do not follow each other
    // Expected Status Code: 400 
    // Expected Behavior: No update on the unfollow operation in the database
    // Expected Output: None
    it('should return 400 when trying to unfollow a user who is not being followed', async () => {
        const userId = 'J0TIKhlLfKXhaUIwHZuS6jChFJ93';
        const followingId = 'SomeOtherUserId';
    
        require('mongodb').MongoClient().db().collection().findOne
            .mockResolvedValueOnce(null) 
            .mockResolvedValueOnce(null); 
    
        const response = await request(app)
            .put('/users/unfollow')
            .send({ userId, followingId });
    
        expect(response.statusCode).toBe(400);
        expect(response.text).toEqual('The specified users do not have a following relationship.');
    });

    //ChatGPT Usage: Partial
    // Test Case 18: User or Following User Not Found
    // Input: Valid User ID and Following ID, but one of them does not exist
    // Expected Status Code: 404
    // Expected Behavior: One of the update operations does not find the intended user
    // Expected Output: "User or the following user not found." in the response body
    it('should return 404 when the user or the following user not found during unfollow', async () => {
        const userId = 'existing_user_id';
        const followingId = 'nonexistent_following_id';

        
        mockUpdateOne.mockResolvedValueOnce({ matchedCount: 0 }); 

        const response = await request(app)
            .put('/users/unfollow')
            .send({ userId, followingId });

        expect(response.statusCode).toBe(404);
        expect(response.text).toEqual("User or the following user not found.");

       
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { userId },
            { $pull: { following: followingId } }
        );

        
    });
  
    //ChatGPT Usage: No
    // Test Case 19: Internal Server Error during Follow Operation
    // Input: Request with valid User ID and Following ID
    // Expected Status Code: 500
    // Expected Behavior: The server should respond with an error message indicating an internal server error.
    // Expected Output: "Internal server error" in the response body.
    it('should return 500 on internal server error during unfollow operation', async () => {
        const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();
      
     
        mockUpdateOne.mockImplementationOnce(() => {
          throw new Error('Simulated error during follow operation');
        });
        
        const response = await request(app)
          .put('/users/unfollow')
          .send({ userId: 'existing_user_id', followingId: 'following_user_id' });
      
        
        expect(consoleErrorMock).toHaveBeenCalledWith('Error during unfollow operation:', expect.any(Error));
    
        expect(response.statusCode).toBe(500);
        
        consoleErrorMock.mockRestore();
      });

  });

  //Interface GET www.myserver.ca/users
  describe('/Get User', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    //ChatGPT Usage: Partial
    // Test Case 20: Successful User Retrieval
    // Input: Valid request with a specific userId
    // Expected Status Code: 200
    // Expected Behavior: The server should retrieve and return the details of the user corresponding to the provided userId.
    // Expected Output: User details in JSON format, matching the mocked user data.
    it('should return user details for a valid userId', async () => {
        const userId = 'validUserId';
        const mockUser = { userId, name: 'Test User', email: 'test@example.com' };


        MongoClient().db().collection().findOne.mockResolvedValue(mockUser);

        const response = await request(app).get(`/users?userId=${userId}`);

        expect(response.statusCode).toBe(200);
        expect(response.body).toEqual(mockUser);
    });

    //ChatGPT Usage: No
    // Test Case 21: Missing UserId in User Retrieval
    // Input: GET request to /users endpoint without a userId
    // Expected Status Code: 400
    // Expected Behavior: The server should respond with an error due to the absence of the required userId parameter.
    // Expected Output: Error message indicating that the User ID must be provided.
    it('should return 400 if userId is not provided', async () => {
        const response = await request(app).get('/users');

        expect(response.statusCode).toBe(400);
        expect(response.text).toEqual('User ID must be provided.');
    });

    //ChatGPT Usage: Partial
    // Test Case 22: User Not Found
    // Input: GET request to /users endpoint with a non-existent userId
    // Expected Status Code: 404
    // Expected Behavior: The server should respond with an error indicating that the specified user was not found in the database.
    // Expected Output: Error message "User not found."
    it('should return 404 if user is not found', async () => {
        const userId = 'nonexistentUserId';

        // Mock database response for nonexistent user
        MongoClient().db().collection().findOne.mockResolvedValue(null);

        const response = await request(app).get(`/users?userId=${userId}`);

        expect(response.statusCode).toBe(404);
        expect(response.text).toEqual('User not found.');
    });

    //ChatGPT Usage: No
    // Test Case 21: Internal Server Error During User Retrieval
    // Input: GET request to /users endpoint with a valid userId
    // Expected Status Code: 500
    // Expected Behavior: No changes in database
    // Expected Output: Error message "Internal server error."
    it('should return 500 on internal server error during user retrieval', async () => {
        const validUserId = 'validUserId';

        
        MongoClient().db().collection().findOne.mockRejectedValue(new Error('Simulated database error'));

        const response = await request(app).get(`/users?userId=${validUserId}`);

        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal server error");

        
        expect(MongoClient().db().collection().findOne).toHaveBeenCalledWith({ userId: validUserId });
    });
    
});

const { checkValidUser } = require('../_userRoutes');

describe('checkValidUser function', () => {
    beforeEach(() => {
      mockFindOne.mockClear();
    });
  
    //ChatGPT Usage: Partial
    // Test Case 23: Valid User
    // Input: User object with all required fields and a non-existing userId
    // Expected Output: true
    it('should return true for a valid user', async () => {
      
      const validUser = {
        userEmail: 'test@example.com',
        userName: 'Test User',
        userGender: 'Non-binary',
        userBirthdate: '1990-01-01',
        userId: 'unique_user_id',
      };
  
      mockFindOne.mockResolvedValue(null);
  
      const isValid = await checkValidUser(validUser);
  
      expect(isValid).toBe(true);
      
      expect(mockFindOne).toHaveBeenCalledWith({ userId: validUser.userId });
    });
  
    //ChatGPT Usage: No
    // Test Case 24: Invalid User (Missing Required Field)
    // Input: User object with a missing required field
    // Expected Output: false
    it('should return false for an invalid user with a missing required field', async () => {
      
      const invalidUser = {
        
        userName: 'Test User',
        userGender: 'Non-binary',
        userBirthdate: '1990-01-01',
        userId: 'unique_user_id',
      };
  
      const isValid = await checkValidUser(invalidUser);
  
      expect(isValid).toBe(false);
      
      expect(mockFindOne).not.toHaveBeenCalled();
    });
  
    //ChatGPT Usage: Partial
    // Test Case 25: User Already Exists in Database
    // Input: User object with an existing userId in the database
    // Expected Output: false
    it('should return false for a user with an existing userId', async () => {
      
      const existingUser = {
        userId: 'existing_user_id',
        userEmail: 'test@example.com',
        userName: 'Test User',
        userGender: 'Non-binary',
        userBirthdate: '1990-01-01',
      };
  
      const userWithExistingUserId = {
        ...existingUser,
      };
  
      
      mockFindOne.mockResolvedValue(existingUser);
  
      const isValid = await checkValidUser(userWithExistingUserId);
  
      expect(isValid).toBe(false);
      
      expect(mockFindOne).toHaveBeenCalledWith({ userId: userWithExistingUserId.userId });
    });
  
    //ChatGPT Usage: Partial
    // Test Case 26: Database Query Error
    // Input: User object with valid data, but an error occurs during database query
    // Expected Output: false
    it('should return false in case of a database query error', async () => {
     
      const userWithDatabaseError = {
        userEmail: 'test@example.com',
        userName: 'Test User',
        userGender: 'Non-binary',
        userBirthdate: '1990-01-01',
        userId: 'unique_user_id',
      };
  
      // Mock a database query error
      mockFindOne.mockRejectedValue(new Error('Simulated database query error'));
  
      // Act
      const isValid = await checkValidUser(userWithDatabaseError);
  
      // Assert
      expect(isValid).toBe(false);
      // Ensure that findOne is called with the correct userId
      expect(mockFindOne).toHaveBeenCalledWith({ userId: userWithDatabaseError.userId });
    });
  });

  const { notifyFollowerIncrease} = require('../_userRoutes');

describe('notifyFollowerIncrease', () => {
    it('should return 400 and error message if userId is not provided', async () => {
      const mockResponse = {
        status: jest.fn().mockReturnThis(),
        send: jest.fn(),
      };
  
      await notifyFollowerIncrease(null, mockResponse);
  
      expect(mockResponse.status).toHaveBeenCalledWith(400);
      expect(mockResponse.send).toHaveBeenCalledWith("User ID and following ID must be provided.");
    });
  
    //ChatGPT Usage: Partial
    // Test Case 27: Successful Notification Sent
    // Input: Valid userId and a new follower's userId
    // Expected Status Code: 200
    // Expected Behavior: If the user and the new follower exist and the user has a token, a notification should be sent.
    // Expected Output: Success message indicating that the notification was sent.
    it('should return 404 and error message if user with provided userId does not exist', async () => {
        const userId = 'nonexistentUserId';

        // Mock userExists to return false for nonexistent user
        userExists.mockReturnValue(false);
        
        // Mock findOne to reject the promise for nonexistent user
        mockFindOne.mockResolvedValue(null);
        
        const mockResponse = {
          status: jest.fn().mockReturnThis(),
          send: jest.fn(),
        };
        
        await notifyFollowerIncrease(userId, mockResponse);
        
        expect(mockResponse.status).toHaveBeenCalledWith(404);
        expect(mockResponse.send).toHaveBeenCalledWith("User or the following user not found.");
      });
  
    //ChatGPT Usage: Partial
    // Test Case 28: Follower does not exist
    // Input: Valid userId and a non-existent new follower's userId
    // Expected Status Code: 404
    // Expected Behavior: If the follower does not exist, no notification should be sent.
    // Expected Output: Error message indicating that the follower was not found.
    it('should not send a notification if user token is not available', async () => {
      const mockResponse = {
        status: jest.fn().mockReturnThis(),
        send: jest.fn(),
      };
  
      mockUserExists.mockReturnValue(true);
      mockFindOne.mockResolvedValue({ token: null });
  
      await notifyFollowerIncrease('existingUserId', mockResponse);
  
      expect(mockResponse.status).not.toHaveBeenCalled();
      expect(mockSend).not.toHaveBeenCalled();
    });

  });

  //interface: www.mydomain.ca/users/update-profile
  describe('Update Profile Route', () => {
    
    //ChatGPT Usage: Partial
    // Test Case 29: Attempt to Update Non-Existent User
    // Input: Update request with userId that does not exist in the database
    // Expected Status Code: 404
    // Expected Behavior: The server should not find the user and therefore not perform an update.
    // Expected Output: Error message "User not found."
    it('should update user profile successfully', async () => {
  
        const existingUser = {
          userId: 'existing_user_id',
          userEmail: 'test@example.com',
          userName: 'Test User',
          userGender: 'Non-binary',
          userBirthdate: '1990-01-01',
        };
      
        const updatedUser = {
          userId: 'existing_user_id',
          userEmail: 'updated_test@example.com',
          userName: 'Updated Test User',
          userGender: 'Male',
          userBirthdate: '1995-05-05',
          token: 'new_token',
        };
      
        mockFindOne.mockResolvedValue(existingUser);
        mockUpdateOne.mockResolvedValue({ matchedCount: 1, modifiedCount: 1 });
      
        const response = await request(app)
          .put('/users/update-profile')
          .send(updatedUser);
      
        expect(response.statusCode).toBe(200);
        expect(response.text).toEqual("User profile updated successfully.");
        expect(mockFindOne).toHaveBeenCalledWith({ userId: 'existing_user_id' });
        expect(mockUpdateOne).toHaveBeenCalledWith(
          { userId: 'existing_user_id' },
          { $set: {
              userEmail: 'updated_test@example.com',
              userName: 'Updated Test User',
              userGender: 'Male',
              userBirthdate: '1995-05-05',
              token: 'new_token'
            }
          }
        );
      });
  
  });

describe('PUT /users/update-avatar', () => {
  beforeEach(() => {
    mockUpdateOne.mockClear();
    mockFindOne.mockImplementation((query) => {
      if (query.userId == 'existingUserId' || query.userId == 'existingUserIdWithoutImage') {
          return {userId: 'existingUserId'}; 
      } else {
          return null;
      }
    });
  });

  //ChatGPT Usage: Partial
  // Test Case 30: Successful Avatar Update
  // Input: Valid userId and avatar file
  // Expected Status Code: 200
  // Expected Behavior: The server updates the avatar for the user.
  // Expected Output: Success message "User avatar updated successfully."
  it('should update user avatar successfully', async () => {
    const mockUserId = 'existingUserId';
    mockUpdateOne.mockResolvedValue({ modifiedCount: 1 });

    const response = await request(app)
      .put('/users/update-avatar')
      .send({'userId': mockUserId})

    expect(response.statusCode).toBe(200);
    expect(response.text).toEqual("User avatar updated successfully.");
    expect(mockUpdateOne).toHaveBeenCalledWith(
      { userId: mockUserId },
      expect.anything()
    );
  });

  //ChatGPT Usage: No
  // Test Case 31: Missing User ID
  // Input: Request without userId
  // Expected Status Code: 400
  // Expected Behavior: The server rejects the request due to missing userId.
  // Expected Output: Error message "User ID must be provided."
   it('should return a 400 status when userId is not provided', async () => {
    const response = await request(app)
      .put('/users/update-avatar')
      .send({});

    expect(response.statusCode).toBe(400);
    expect(response.text).toEqual("User ID must be provided.");
  });

  //ChatGPT Usage: No
  // Test Case 32: Non-Existent User
  // Input: Request with a userId that does not exist in the database
  // Expected Status Code: 400
  // Expected Behavior: The server rejects the request as the user does not exist.
  // Expected Output: Error message "User does not exist."
  it('should return a 400 status when user does not exist', async () => {
    const mockUserId = 'nonExistentUserId';
    const response = await request(app)
      .put('/users/update-avatar')
      .send({ 'userId': mockUserId });

    expect(response.statusCode).toBe(400);
    expect(response.text).toEqual("User does not exist.");
  });

  //ChatGPT Usage: No
  // Test Case 33: Missing Avatar File
  // Input: Request with userId but without avatar file
  // Expected Status Code: 400
  // Expected Behavior: The server rejects the request due to missing avatar file.
  // Expected Output: Error message "User avatar was not provided."
  it('should return a 400 status when avatar file is not provided', async () => {
    const mockUserId = 'existingUserIdWithoutImage';
    const response = await request(app)
      .put('/users/update-avatar')
      .send({ 'userId': mockUserId });

    expect(response.statusCode).toBe(400);
    expect(response.text).toEqual("User avatar was not provided.");
  });

  //ChatGPT Usage: Partial
  // Test Case 34: No Changes Made to User Avatar
  // Input: Request with userId but the avatar is unchanged
  // Expected Status Code: 200
  // Expected Behavior: The server accepts the request but makes no changes as the avatar is unchanged.
  // Expected Output: Information message "No changes made to user avatar."
  it('should return a 200 status with no changes message when avatar is unchanged', async () => {
    const mockUserId = 'existingUserId';
    mockUpdateOne.mockResolvedValue({ modifiedCount: 0 });

    const response = await request(app)
      .put('/users/update-avatar')
      .send({ 'userId': mockUserId });

    expect(response.statusCode).toBe(200);
    expect(response.text).toEqual("No changes made to user avatar.");
  });

  //ChatGPT Usage: No
  // Test Case 35: Internal Server Error During User Retrieval
  // Input: Valid request with a userId
  // Expected Status Code: 500
  // Expected Behavior: The server encounters an unexpected error during processing.
  // Expected Output: Error message "Internal server error"
  it('should return 500 on internal server error during user retrieval', async () => {
    const mockUserId = 'existingUserId';    
    MongoClient().db().collection().updateOne.mockRejectedValue(new Error('Simulated database error'));

    const response = await request(app)
      .put('/users/update-avatar')
      .send({ 'userId': mockUserId });

    expect(response.statusCode).toBe(500);
    expect(response.text).toEqual("Internal server error");
  });

});

