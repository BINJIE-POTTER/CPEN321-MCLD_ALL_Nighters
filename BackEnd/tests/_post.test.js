const request = require('supertest');
const { MongoClient } = require('mongodb');
const uuid = require('uuid');

// Mock MongoDB Methods
const mockInsertOne = jest.fn();
const mockUpdateOne = jest.fn();
const mockFindOne = jest.fn();
const mockDeleteOne = jest.fn();
const mockFind = jest.fn();

// Mock MongoDB Client
jest.mock('mongodb', () => ({
    MongoClient: jest.fn().mockImplementation(() => ({
        connect: jest.fn().mockResolvedValue(this),
        db: jest.fn().mockReturnValue({
            collection: jest.fn().mockReturnValue({
            findOne: mockFindOne,
            find: mockFind,
            insertOne: mockInsertOne,
            updateOne: mockUpdateOne,
            deleteOne: mockDeleteOne,
            }),
        }),
        close: jest.fn(),
    })),
}));

// Mock UUID
jest.mock('uuid', () => ({
    v4: jest.fn().mockReturnValue('unique_post_id')
}));

const app = require('../server');

// Prepare the sample user data
const userSample = {
    "token": "12345",
    "userBirthdate": "none",
    "userEmail": "12345@123.com",
    "userGender": "none",
    "userId": "12345",
    "userName": "User"
};

// Interface POST www.myserver.ca/posts
describe('Create a post', () => {

    jest.setTimeout(5000); // 10 seconds in milliseconds

    beforeEach(() => {
        mockInsertOne.mockClear();
        mockUpdateOne.mockClear();
        mockFindOne.mockClear(); // Clear the mock user retrieval
        uuid.v4.mockClear();
    });

    //ChatGPT Usage: Partial
    //Test Case 1: Valid Post Submission
    //Input: Valid post with a title, content, time, coordinate, and userId
    //Expected Status Code: 200
    //Expected Behavior: Post is added to the database, tags are generated and added, user post count is incremented.
    //Expected Output: Item Received Successfully
    it('Valid Post Submission', async () => {
        // Set up mock user retrieval to return the userSample

        mockFindOne.mockResolvedValue(userSample);

        mockUpdateOne.mockImplementation(async (filter, update) => {
            const mockResponse = {
                acknowledged: true,
                modifiedCount: 1,
            };
            return mockResponse;
        });

        const newPost = {
            userId: '12345', // Use the userId from userSample
            content: {
                title: 'Test Post',
                body: 'This is a test post.',
                tags:  []
            },
            time: new Date().toISOString(),
            coordinate: {
                latitude: 45.4215,
                longitude: -75.6972
            }
        };

        const expectedPost = {
            userId: '12345', // Use the userId from userSample
            content: {
                title: 'Test Post',
                body: 'This is a test post.',
                tags:  ["Test", "Post"]
            },
            time: new Date().toISOString(),
            coordinate: {
                latitude: 45.4215,
                longitude: -75.6972
            },
            pid: 'unique_post_id',
            likeCount: 0,
            likeList: [],
            image: {
                contentType: "",
                image: ""
            }
        }

        const response = await request(app)
            .post('/posts')
            .send(newPost);

        expect(response.statusCode).toBe(200);
        expect(response.text).toEqual("Item received successfully");
        expect(uuid.v4).toHaveBeenCalled();
        expect(mockInsertOne).toHaveBeenCalledWith({
            ...expectedPost,
        });
        expect(mockInsertOne).toHaveBeenCalledWith(
            expect.objectContaining({
                tagName: "Test"
            })
        );
        expect(mockInsertOne).toHaveBeenCalledWith(
            expect.objectContaining({
                tagName: "Post"
            })
        );
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { userId: '12345' }, // Ensure the correct userId is used
            { $inc: { postCount: 1 } }
        );
    });

    //ChatGPT Usage: Partial
    //Test Case 2: Invalid Post Format
    //Input: Invalid post data (e.g, missing fields or incorrect structure)
    //Expected Status Code: 400
    //Expected Behavior: No change in database
    //Expected Output: None
    it('Invalid Post Format', async () => {
        // Define an invalid post format
        const invalidPost = {
            userId: '12345',
        };

        const response = await request(app)
            .post('/posts')
            .send(invalidPost);

        expect(response.statusCode).toBe(400);
        expect(response.text).toEqual("Invalid post format");
    });

    //ChatGPT Usage: No
    // Test Case 3: Handling Internal Server Error
    // Input: POST request to create a new post
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an internal error while processing the request.
    // Expected Output: Error message "Internal Server Error".
    it('should return 500 for internal server error', async () => {
        
        mockInsertOne.mockRejectedValue(new Error("Simulated internal server error"));

        const newPost = {
            userId: '12345', // Use the userId from userSample
            content: {
                title: 'Test Post',
                body: 'This is a test post.',
                tags:  []
            },
            time: new Date().toISOString(),
            coordinate: {
                latitude: 45.4215,
                longitude: -75.6972
            }
        };

        const response = await request(app)
            .post('/posts')
            .send(newPost);

        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal Server Error");
    });
});

//Interface DELETE www.mydomain.ca/posts
describe('Delete a post by pid', () => {
    beforeEach(() => {
        mockDeleteOne.mockClear();
        mockDeleteOne.mockImplementation(async ({ pid }) => {
            if (pid === 'unique_post_id') {
                return {deletedCount: 1};
            } else {
                return {deletedCount: 0}; // Return null for non-matching pids
            }
        }); 
    });

    //ChatGPT Usage: Partial
    // Test Case 4: Successful Post Deletion
    // Input: DELETE request with a valid post ID (pid)
    // Expected Status Code: 200
    // Expected Behavior: The server successfully deletes the post with the given pid.
    // Expected Output: Success message "Post deleted successfully."
    it('should delete a post when provided with a valid pid', async () => {

        const response = await request(app)
        .delete('/posts')
        .query({ pid: 'unique_post_id' });

        expect(response.statusCode).toBe(200);
        expect(response.text).toEqual('Post deleted successfully.');
        expect(mockDeleteOne).toHaveBeenCalledWith({ pid: 'unique_post_id' });
    });

    //ChatGPT Usage: Partial
    // Test Case 5: Missing Post ID (PID)
    // Input: DELETE request without a post ID (pid)
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing pid.
    // Expected Output: Error message "Missing post ID (pid)".
    it('Missing PID', async () => {
        const response = await request(app)
        .delete('/posts');

        expect(response.statusCode).toBe(400);
        expect(response.text).toEqual('Missing post ID (pid)');
        expect(mockDeleteOne).not.toHaveBeenCalled();
    });

    //ChatGPT Usage: Partial
    // Test Case 6: No Post Found With PID
    // Input: DELETE request with a non-existent post ID (pid)
    // Expected Status Code: 404
    // Expected Behavior: The server returns an error when no post is found with the given pid.
    // Expected Output: Error message "No post found with the given ID."
    it('No Post Found With PID', async () => {

        const response = await request(app)
        .delete('/posts')
        .query({ pid: 'non_existent_post_id' });

        expect(response.statusCode).toBe(404);
        expect(response.text).toEqual('No post found with the given ID.');
        expect(mockDeleteOne).toHaveBeenCalledWith({ pid: 'non_existent_post_id' });
    });

    //ChatGPT Usage: No
    // Test Case 7: Handling Internal Server Error
    // Input: DELETE request where an internal server error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an internal error and logs it.
    // Expected Output: Error message "Internal Server Error".
    it('Internal Server Error', async () => {

        mockDeleteOne.mockRejectedValue(new Error("Simulated internal server error"));

        const response = await request(app)
        .delete('/posts')
        .query({ pid: 'unique_post_id' });

        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal Server Error");
    });
});


// Interface GET www.myserver.ca/posts/single
describe('Get a post by pid', () => {

    const expectedPost = {
        userId: '12345', // Use the userId from userSample
        content: {
            title: 'Test Post',
            body: 'This is a test post.',
            tags:  ["test", "post"]
        },
        time: new Date().toISOString(),
        coordinate: {
            latitude: 45.4215,
            longitude: -75.6972
        },
        pid: 'unique_post_id',
        likeCount: 0,
        likeList: [],
        image: {
            contentType: "",
            image: ""
        }
    }

    beforeEach(() => {
        mockFindOne.mockClear();
        mockFindOne.mockImplementation(async ({ pid }) => {
            if (pid === 'unique_post_id') {
                return expectedPost;
            } else {
                return null; // Return null for non-matching pids
            }
        });  
    });

    //ChatGPT Usage: Partial
    // Test Case 8: Post Found With Valid PID
    // Input: GET request with a valid post ID (pid)
    // Expected Status Code: 200
    // Expected Behavior: The server retrieves and returns the post matching the given pid.
    // Expected Output: The expected post data.
    it('Post Found With Valid PID', async () => {

        const response = await request(app)
            .get('/posts/single')
            .query({ pid: 'unique_post_id' }); // Provide a valid pid here
    
        expect(response.statusCode).toBe(200);
        expect(response.body).toEqual(expectedPost);
        expect(mockFindOne).toHaveBeenCalledWith({ pid: 'unique_post_id' });
    });

    //ChatGPT Usage: Partial
    // Test Case 9: Missing PID
    // Input: GET request without a post ID (pid)
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing pid.
    // Expected Output: Error message "Pid is required".
    it('Missing PID', async () => {
      const response = await request(app)
        .get('/posts/single');
  
      expect(response.statusCode).toBe(400);
      expect(response.text).toEqual("Pid is required");
      expect(mockFindOne).not.toHaveBeenCalled();
    });
  
    //ChatGPT Usage: Partial
    // Test Case 10: No Post Found With PID
    // Input: GET request with a non-existent post ID (pid)
    // Expected Status Code: 404
    // Expected Behavior: The server returns an error when no post is found with the given pid.
    // Expected Output: Error message "Post not found".
    it('No Post Found With PID', async () => {
      // Mock the findOne method to resolve with null, indicating no post found
      mockFindOne.mockResolvedValue(null);
  
      const response = await request(app)
        .get('/posts/single')
        .query({ pid: 'non_existent_post_id' }); 
  
      expect(response.statusCode).toBe(404);
      expect(response.text).toEqual("Post not found");
      expect(mockFindOne).toHaveBeenCalledWith({ pid: 'non_existent_post_id' });
    });

    //ChatGPT Usage: No
    // Test Case 11: Handling Internal Server Error
    // Input: GET request where an internal server error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an internal error and logs it.
    // Expected Output: Error message "Internal Server Error".
    it('Internal Server Error', async () => {

        mockFindOne.mockRejectedValue(new Error("Simulated internal server error"));

        const response = await request(app)
        .get('/posts/single')
        .query({ pid: 'unique_post_id' });

        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal Server Error");
    });
});

//Interface GET www.mydomain.ca/posts/cluster
describe('Get clustered posts', () => {
    const userLatitude = 45.4215;
    const userLongitude = -75.6972;
    const samplePosts = [
      {
        coordinate: { latitude: 45.4215, longitude: -75.6972 },
      },
      {
        coordinate: { latitude: 45.4216, longitude: -75.6971 },
      },
    ];

    beforeEach(() => {
      mockFind.mockClear();
    });

    //ChatGPT Usage: Partial
    // Test Case 12: Return Clustered Posts for Valid User Coordinates
    // Input: GET request with valid user latitude and longitude
    // Expected Status Code: 200
    // Expected Behavior: The server returns posts clustered based on the provided coordinates.
    // Expected Output: Clustered posts data.
    it('should return clustered posts for valid user coordinates', async () => {
        // Mock the find method to return the sample posts
        mockFind.mockReturnValue({
            toArray: () => samplePosts,
        });
  
        // Define your expected clusters based on the sample data
        const expectedClusters = [
            {
            clusterId: 0,
            latitude: 45.4215,
            longitude: -75.6972,
            posts: [samplePosts[0], samplePosts[1]],
            },
        ];
  
        // Make a request to the route
        const response = await request(app)
            .get('/posts/cluster')
            .query({ latitude: userLatitude, longitude: userLongitude });
  
        expect(response.status).toBe(200);
        expect(response.body).toEqual(expectedClusters);
    });
  
    //ChatGPT Usage: No
    // Test Case 13: Handle Missing User Coordinates
    // Input: GET request without user latitude and longitude
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing coordinates.
    // Expected Output: Error message "Missing user latitude or longitude".
    it('should handle missing user coordinates', async () => {
        const response = await request(app).get('/posts/cluster');
        expect(response.status).toBe(400);
        expect(response.text).toEqual('Missing user latitude or longitude');
    });

    //ChatGPT Usage: No
    // Test Case 14: Return a 500 Status on Internal Server Error
    // Input: GET request where an internal server error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an internal error and logs it.
    // Expected Output: Error message "Internal Server Error".
    it('should return a 500 status on internal server error', async () => {
        mockFind.mockImplementation(() => {
            throw new Error("Database error");
        });
    
        const response = await request(app)
          .get('/posts/cluster')
          .query({ latitude: userLatitude, longitude: userLongitude });
    
        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal Server Error");
      });

});


//Interface GET www.mydomain.ca/posts/from-user
describe('Get posts from a specific user', () => {
    const samplePosts = [
        {  userId: '123' },
        {  userId: '456' },
    ];

    beforeEach(() => {
        mockFind.mockClear();
        mockFind.mockReturnValue({
            toArray: () => samplePosts,
        });
    });
    
    //ChatGPT Usage: Partial
    // Test Case 15: Return Posts from a Specific User for a Valid UserId
    // Input: GET request with a valid userId
    // Expected Status Code: 200
    // Expected Behavior: The server retrieves and returns posts belonging to the specified user.
    // Expected Output: Array of posts from the specified user.
    it('should return posts from a specific user for a valid userId', async () => {
        const response = await request(app)
            .get('/posts/from-user')
            .query({ userId: '123'});
        expect(response.status).toBe(200);
        expect(response.body).toEqual(samplePosts.filter(post => post.userId === '123'));
    });

    //ChatGPT Usage: No
    // Test Case 16: Handle Missing UserId in the Query
    // Input: GET request without a userId
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing userId.
    // Expected Output: Error message "Missing user_id (userId)".
    it('should handle missing userId in the query', async () => {
        const response = await request(app).get('/posts/from-user');
        expect(response.status).toBe(400);
        expect(response.text).toEqual('Missing user_id (userId)');
    });

    //ChatGPT Usage: Partial
    // Test Case 17: Return an Empty Array for Non-Existent UserId
    // Input: GET request with a userId that has no posts
    // Expected Status Code: 200
    // Expected Behavior: The server returns an empty array if the specified user has no posts.
    // Expected Output: An empty array.
    it('should return an empty array if no posts are found for the specified userId', async () => {
        mockFind.mockReturnValue({
            toArray: () => [],
        });
        const userIdWithNoPosts = '789';
        const response = await request(app)
            .get('/posts/from-user')
            .query({ userId: userIdWithNoPosts });
        expect(response.status).toBe(200);
        expect(response.body).toEqual([]);
    });

    //ChatGPT Usage: No
    // Test Case 18: Return a 500 Status on Internal Server Error
    // Input: GET request where an internal server error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an internal error and logs it.
    // Expected Output: Error message "Internal Server Error".
    it('should return a 500 status on internal server error', async () => {
        mockFind.mockImplementation(() => {
            throw new Error("Database error");
        });
    
        const response = await request(app)
          .get('/posts/from-user')
          .query({ userId: '123'});
     
        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal Server Error");
    });
});


//Interface GET www.mydomain.ca/posts/has-tags
describe('GET /posts/has-tags', () => {

    const samplePosts = [
        {
            userId: 'user1',
            content: {
            tags: ['tag1', 'tag2'],
            },
            coordinate: {
            latitude: 45.4215,
            longitude: -75.6972,
            },
        },
        {
            userId: 'user2',
            content: {
            tags: ['tag2', 'tag3'],
            },
            coordinate: {
            latitude: 45.422,
            longitude: -75.698,
            },
        },
    ];

    beforeEach(() => {
        mockFind.mockClear();
        mockFind.mockReturnValue({
            toArray: () => samplePosts,
        });
    });
  
    //ChatGPT Usage: Partial
    // Test Case 19: Get Posts of Wanted Tags
    // Input: GET request with specific tag(s) and location coordinates
    // Expected Status Code: 200
    // Expected Behavior: The server retrieves and returns posts that include the specified tags.
    // Expected Output: Array of posts containing the specified tags.
    it('Get Posts Of Wanted Tags', async () => {  
      const query = {
        tags: 'tag2',
        latitude: 45.4215,
        longitude: -75.6972,
      };
  
      const response = await request(app)
        .get('/posts/has-tags')
        .query(query);
  
      expect(response.status).toBe(200);
      expect(response.body).toEqual(samplePosts);
    });
  
    //ChatGPT Usage: No
    // Test Case 20: Get Posts of a Specific Tag
    // Input: GET request with a specific tag and location coordinates
    // Expected Status Code: 200
    // Expected Behavior: The server retrieves and returns posts that include the specific tag.
    // Expected Output: Array of posts containing the specific tag.
    it('Get Posts Of Wanted Tags 2', async () => {  
        const query = {
          tags: 'tag1',
          latitude: 45.4215,
          longitude: -75.6972,
        };
    
        const response = await request(app)
          .get('/posts/has-tags')
          .query(query);
    
        expect(response.status).toBe(200);
        expect(response.body).toEqual([samplePosts[0]]);
      });

    //ChatGPT Usage: No
    // Test Case 21: Handle Missing Query Parameters
    // Input: GET request without any query parameters
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing query parameters.
    // Expected Output: Error message indicating missing parameters.
    it('should return a 400 status when query parameters are missing', async () => {
        const response = await request(app)
        .get('/posts/has-tags');

        expect(response.status).toBe(400);
    });

    //ChatGPT Usage: No
    // Test Case 22: Handle Missing Tags Query Parameter
    // Input: GET request with location coordinates but missing tags parameter
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing tags parameter.
    // Expected Output: Error message indicating missing tags parameter.
    it('should return a 400 status when tags query parameter is missing', async () => {
        const query = {
        latitude: 45.4215,
        longitude: -75.6972,
        };

        const response = await request(app)
        .get('/posts/has-tags')
        .query(query);

        expect(response.status).toBe(400);
    });
  
    //ChatGPT Usage: No
    // Test Case 22: Handle Internal Server Error
    // Input: GET request where an internal server error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an internal error and logs it.
    // Expected Output: Error message "Internal Server Error".
    it('should return a 500 status on internal server error', async () => {
        mockFind.mockImplementation(() => {
            throw new Error("Database error");
        });
    
        const query = {
            tags: 'tag2',
            latitude: 45.4215,
            longitude: -75.6972,
          };
      
          const response = await request(app)
            .get('/posts/has-tags')
            .query(query);
     
        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal Server Error");
    });

});

//Interface GET www.mydomain.ca/posts/search
describe('GET /posts/search', () => {

    const samplePosts = [
        {
          pid: 'post1',
          content: {
            title: 'This is a sample post title',
          },
        },
        {
          pid: 'post2',
          content: {
            title: 'Another sample title',
          },
        },
        // Add more sample posts as needed
      ];

    beforeEach(() => {
      mockFind.mockClear();
      mockFind.mockReturnValue({
        toArray: () => samplePosts,
    });
    });
 
    //ChatGPT Usage: Partial
    // Test Case 23: Return Posts Matching the Keyword with a Valid Keyword
    // Input: GET request with a keyword present in one or more post titles
    // Expected Status Code: 200
    // Expected Behavior: The server retrieves and returns posts that match the given keyword.
    // Expected Output: Array of posts matching the keyword.
    it('should return posts matching the keyword when a valid keyword is provided', async () => {
      const keyword = 'sample';
  
      const response = await request(app)
        .get('/posts/search')
        .query({ keyword });
  
      expect(response.status).toBe(200);
      expect(response.body).toEqual(samplePosts);
    });
    
    //ChatGPT Usage: No
    // Test Case 24: Return Specific Posts for a Different Valid Keyword
    // Input: GET request with a different keyword
    // Expected Status Code: 200
    // Expected Behavior: The server retrieves and returns specific posts that match the different keyword.
    // Expected Output: Array of specific posts matching the different keyword.
    it('should return posts matching the keyword when a valid keyword is provided', async () => {
        const keyword = 'another';
    
        const response = await request(app)
          .get('/posts/search')
          .query({ keyword });
    
        expect(response.status).toBe(200);
        expect(response.body).toEqual([samplePosts[1]]);
    });

    //ChatGPT Usage: No
    // Test Case 25: Handle Missing Keyword Query Parameter
    // Input: GET request without a keyword parameter
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing keyword.
    // Expected Output: Error message indicating missing keyword parameter.
    it('should return a 400 status when the keyword query parameter is missing', async () => {
      const response = await request(app)
        .get('/posts/search');
  
      expect(response.status).toBe(400);
    });

    //ChatGPT Usage: No
    // Test Case 26: Handle Internal Server Error
    // Input: GET request where an internal server error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an internal error and logs it.
    // Expected Output: Error message "Internal Server Error".
    it('should return a 500 status on internal server error', async () => {
        mockFind.mockImplementation(() => {
            throw new Error("Database error");
        });
    
        const keyword = 'sample';
  
        const response = await request(app)
          .get('/posts/search')
          .query({ keyword });
     
        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal Server Error");
    });
});

//Interface PUT www.mydomain.ca/posts/like
describe('PUT /posts/like', () => {

    beforeEach(() => {
        var samplePosts = [
            {
                userId: '12345',
                content: {
                    title: 'Test Post',
                    body: 'This is a test post.',
                    tags:  ["test", "post"]
                },
                time: new Date().toISOString(),
                coordinate: {
                    latitude: 45.4215,
                    longitude: -75.6972
                },
                pid: 'unique_post_id',
                likeCount: 0,
                likeList: [],
                image: {
                    contentType: "",
                    image: ""
                }
            }
        ];

        mockUpdateOne.mockClear();
        mockUpdateOne.mockImplementation(async (pid, update) => {

            const postToUpdate = samplePosts.find(post => post.pid == pid.pid);
            if (!postToUpdate) {
                return { matchedCount: 0 };
            }
            
            let modifiedCount = 0; 
            
            if (update.$inc && update.$inc.likeCount) {
                if (!postToUpdate.likeList.includes(update.$addToSet.likeList)) {
                    postToUpdate.likeCount += update.$inc.likeCount;
                    modifiedCount = 1;
                }
            }
            
            if (update.$addToSet && update.$addToSet.likeList) {
                const userIdToAdd = update.$addToSet.likeList;
                if (!postToUpdate.likeList.includes(userIdToAdd)) {
                    postToUpdate.likeList.push(userIdToAdd);
                    modifiedCount = 1;
                }
            }
            
            return {
                matchedCount: 1,
                modifiedCount,
            };
        });
    });

    //ChatGPT Usage: Partial
    // Test Case 27: Increment Like Count and Add UserId to LikeList
    // Input: PUT request with valid post ID (pid) and user ID (userId)
    // Expected Status Code: 200
    // Expected Behavior: The server updates the like count and like list of the post.
    // Expected Output: Success message "Post's like count and like list updated successfully."
    it('should increment likeCount and add userId to likeList when valid pid and userId are provided', async () => {
        const pid = 'unique_post_id';
        const userId = 'sample_user_id';
        
        const response = await request(app)
            .put('/posts/like')
            .send({ pid, userId });

        expect(response.status).toBe(200);
        expect(response.text).toBe("Post's like count and like list updated successfully.");
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { pid },
            {
                $inc: { likeCount: 1 },
                $addToSet: { likeList: userId },
            }
        );
    });

    //ChatGPT Usage: No
    // Test Case 28: Handle Missing Pid or UserId
    // Input: PUT request without post ID (pid) or user ID (userId)
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing parameters.
    // Expected Output: Error message "Post ID and User ID must be provided."
    it('should return a 400 status when pid or userId is missing', async () => {
        const response = await request(app)
        .put('/posts/like')
        .send({});

        expect(response.status).toBe(400);
        expect(response.text).toBe("Post ID and User ID must be provided.");
    });

    //ChatGPT Usage: No
    // Test Case 29: Handle Non-Existent Post Id
    // Input: PUT request with a non-existent post ID (pid)
    // Expected Status Code: 404
    // Expected Behavior: The server returns an error when no post is found with the given pid.
    // Expected Output: Error message "Post not found."
    it('should return a 404 status when the provided pid does not exist', async () => {
        const pid = 'non_existent_post_id';
        const userId = 'sample_user_id';

        const response = await request(app)
            .put('/posts/like')
            .send({ pid, userId });

        expect(response.status).toBe(404);
        expect(response.text).toBe("Post not found.");
    });

    //ChatGPT Usage: Partial
    // Test Case 30: Handle No Changes to Like Count or Like List
    // Input: PUT request with a post ID (pid) and user ID (userId) that have already been processed
    // Expected Status Code: 200
    // Expected Behavior: The server returns a success status but no changes are made as the user has already liked the post.
    // Expected Output: Message "No changes made to the post's like count or like list."
    it('should return a 200 status when the provided pid exists but no changes are made to like count or like list', async () => {
        const pid = 'unique_post_id';
        const userId = 'sample_user_id';

        var response = await request(app)
            .put('/posts/like')
            .send({ pid, userId });
        expect(response.status).toBe(200);
        expect(response.text).toBe("Post's like count and like list updated successfully.");
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { pid },
            {
                $inc: { likeCount: 1 },
                $addToSet: { likeList: userId },
            }
        );

        var response = await request(app)
        .put('/posts/like')
        .send({ pid, userId });
        expect(response.status).toBe(200);
        expect(response.text).toBe("No changes made to the post's like count or like list.");
    });

    //ChatGPT Usage: No
    // Test Case 31: Handle Internal Server Error
    // Input: PUT request where an internal server error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an internal error and logs it.
    // Expected Output: Error message "Internal Server Error".
    it('should return a 500 status on internal server error', async () => {
        mockUpdateOne.mockImplementation(() => {
            throw new Error("Database error");
        });
    
        const pid = 'unique_post_id';
        const userId = 'sample_user_id';
        
        const response = await request(app)
            .put('/posts/like')
            .send({ pid, userId });
     
        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal Server Error");
    });
});

//Interface PUT www.mydomain.ca/posts/unlike
describe('PUT /posts/unlike', () => {

    beforeEach(() => {
        var samplePosts = [
            {
                userId: '12345',
                content: {
                    title: 'Test Post',
                    body: 'This is a test post.',
                    tags:  ["test", "post"]
                },
                time: new Date().toISOString(),
                coordinate: {
                    latitude: 45.4215,
                    longitude: -75.6972
                },
                pid: 'unique_post_id',
                likeCount: 1,
                likeList: ['sample_user_id'],
                image: {
                    contentType: "",
                    image: ""
                }
            }
        ];

        mockFindOne.mockClear();
        mockFindOne.mockImplementation(async (pid) => {
            return samplePosts.find(post => post.pid == pid.pid);
        })
    
        mockUpdateOne.mockClear();
        mockUpdateOne.mockImplementation(async (pid, update) => {

            const postToUpdate = samplePosts.find(post => post.pid == pid.pid);
            if (!postToUpdate) {
                return { matchedCount: 0 };
            }
            
            let modifiedCount = 0; 
            
            if (update.$inc && update.$inc.likeCount) {
                if (postToUpdate.likeList.includes(update.$pull.likeList)) {
                    postToUpdate.likeCount += update.$inc.likeCount;
                    modifiedCount = 1;
                }
            }
            
            if (update.$pull && update.$pull.likeList) {
                const index = postToUpdate.likeList.indexOf(update.$pull.likeList);
                if (index > -1) {
                    postToUpdate.likeList.splice(index, 1);
                    modifiedCount = 1;
                }
            }
            
            return {
                matchedCount: 1,
                modifiedCount,
            };
        });
    });

    //ChatGPT Usage: Partial
    // Test Case 32: Decrement Like Count and Remove UserId
    // Input: PUT request with valid post ID (pid) and user ID (userId)
    // Expected Status Code: 200
    // Expected Behavior: The server updates the like count and like list of the post by decrementing and removing the user ID.
    // Expected Output: Success message "Post's like count and like list updated successfully."
    it('should decrement likeCount and remove userId from likeList when valid pid and userId are provided', async () => {
        const pid = 'unique_post_id';
        const userId = 'sample_user_id';

        const response = await request(app)
            .put('/posts/unlike')
            .send({ pid, userId });

        expect(response.status).toBe(200);
        expect(response.text).toBe("Post's like count and like list updated successfully.");
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { pid },
            {
                $inc: { likeCount: -1 },
                $pull: { likeList: userId },
            }
        );
    });

    //ChatGPT Usage: No
    // Test Case 33: Handle Missing Pid or UserId
    // Input: PUT request without post ID (pid) or user ID (userId)
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing parameters.
    // Expected Output: Error message "Post ID and User ID must be provided."
    it('should return a 400 status when pid or userId is missing', async () => {
        const response = await request(app)
            .put('/posts/unlike')
            .send({});

        expect(response.status).toBe(400);
        expect(response.text).toBe("Post ID and User ID must be provided.");
    });

    //ChatGPT Usage: No
    // Test Case 34: Handle Non-Existent Post Id
    // Input: PUT request with a non-existent post ID (pid)
    // Expected Status Code: 404
    // Expected Behavior: The server returns an error when no post is found with the given pid.
    // Expected Output: Error message "Post not found."
    it('should return a 404 status when the provided pid does not exist', async () => {
        const pid = 'non_existent_post_id';
        const userId = 'sample_user_id';

        const response = await request(app)
            .put('/posts/unlike')
            .send({ pid, userId });

        expect(response.status).toBe(404);
        expect(response.text).toBe("Post not found.");
    });

    //ChatGPT Usage: Partial
    // Test Case 35: Handle No Changes to Like Count or Like List
    // Input: PUT request with a post ID (pid) and user ID (userId) that have already been processed for unlike
    // Expected Status Code: 409
    // Expected Behavior: The server returns a conflict status as the user cannot unlike the post again.
    // Expected Output: Message "Can't decrease like count below zero."
    it('should return a 200 status when the provided pid exists but no changes are made to like count or like list', async () => {
        const pid = 'unique_post_id';
        const userId = 'sample_user_id';

        var response = await request(app)
            .put('/posts/unlike')
            .send({ pid, userId });

        expect(response.status).toBe(200);
        expect(response.text).toBe("Post's like count and like list updated successfully.");
        expect(mockUpdateOne).toHaveBeenCalledWith(
            { pid },
            {
                $inc: { likeCount: -1 },
                $pull: { likeList: userId },
            }
        );

        var response = await request(app)
            .put('/posts/unlike')
            .send({ pid, userId });
        expect(response.status).toBe(409);
        expect(response.text).toBe("Can't decrease like count below zero.");
    });

    //ChatGPT Usage: No
    // Test Case 36: Handle Internal Server Error
    // Input: PUT request where an internal server error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an internal error and logs it.
    // Expected Output: Error message "Internal Server Error".
    it('should return a 500 status on internal server error', async () => {
        mockUpdateOne.mockImplementation(() => {
            throw new Error("Database error");
        });
    
        const pid = 'unique_post_id';
        const userId = 'sample_user_id';
        
        const response = await request(app)
            .put('/posts/unlike')
            .send({ pid, userId });
     
        expect(response.statusCode).toBe(500);
        expect(response.text).toEqual("Internal Server Error");
    });

});


const { generateTags } = require('../_postRoutes');

describe('Tag Generation Consistency Tests', () => {
    // Test for tag generation consistency
    it('should generate tags with at least 90% similarity for identical posts', async () => {
        // Same content for two posts
        const postContent = 'This is a sample post to test tag generation consistency.';

        // Generate tags for the same post twice
        const generatedTags1 = await generateTags(postContent);
        const generatedTags2 = await generateTags(postContent);

        // Function to calculate the similarity percentage of tags
        const calculateTagSimilarity = (tags1, tags2) => {
            const set1 = new Set(tags1.split(',').map(tag => tag.trim()));
            const set2 = new Set(tags2.split(',').map(tag => tag.trim()));
            const intersection = new Set([...set1].filter(tag => set2.has(tag)));
            return (intersection.size / Math.min(set1.size, set2.size)) * 100;
        };

        // Calculate similarity percentage
        const similarityPercentage = calculateTagSimilarity(generatedTags1, generatedTags2);

        // Check if similarity is at least 90%
        expect(similarityPercentage).toBeGreaterThanOrEqual(90);
    });
});