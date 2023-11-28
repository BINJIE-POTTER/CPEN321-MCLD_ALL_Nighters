const request = require('supertest');
const { MongoClient } = require('mongodb');

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

const app = require('../server');

//Interface: www.mydomain.ca/tags
describe('GET /tags', () => {

    const sampleTags = [
        {
            tagName: "Paris"
        },
        {
            tagName: "travel"
        }
    ];

    beforeEach(() => {
        mockFind.mockClear();
    });

    //ChatGPT Usage: Partial
    // Test Case 1: Successful Retrieval of All Tags
    // Input: GET request to /tags endpoint
    // Expected Status Code: 200
    // Expected Behavior: The server retrieves all tags from the database.
    // Expected Output: Array of tags.
    it('should return all tags with a 200 status code', async () => {
        mockFind.mockReturnValue({
            toArray: () => sampleTags,
        }); 

        const response = await request(app).get('/tags');
        expect(response.status).toBe(200);
        expect(response.body).toEqual(sampleTags);
    });
  
    //ChatGPT Usage: No
    // Test Case 2: Handling Errors and Returning a 500 Status Code
    // Input: GET request to /tags endpoint where an error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an error and logs it.
    // Expected Output: Error message and a 500 status code.
    it('should handle errors and return a 500 status code', async () => {
      // Mocking an error in your route by throwing an exception
        const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();

        mockFind.mockImplementationOnce(() => {
            throw new Error("Internal Server Error");
        });
        const response = await request(app).get('/tags');
        expect(response.status).toBe(500);
        expect(consoleErrorMock).toHaveBeenCalledWith('Internal Server Error');
    });

});


describe('GET /tags/nearby', () => {

    const samplePosts = [
        {
            userId: 'user1',
            content: {
            tags: ['tag1', 'tag2'],
            },
            coordinate: {
            latitude: 50,
            longitude: 50,
            },
        },
        {
            userId: 'user2',
            content: {
            tags: ['tag2', 'tag3'],
            },
            coordinate: {
            latitude: -50,
            longitude: -50,
            },
        },
    ];

    beforeEach(() => {
        mockFind.mockClear();
    });

    //ChatGPT Usage: Partial
    // Test Case 3: Successful Retrieval of Nearby Tags
    // Input: GET request with valid latitude and longitude
    // Expected Status Code: 200
    // Expected Behavior: The server retrieves tags from posts that are geographically nearby the provided coordinates.
    // Expected Output: Array of tags from nearby posts.
    it('should return tags for nearby posts', async () => {

        const expectedTags = ["tag1","tag2"];

        mockFind.mockReturnValue({
            toArray: () => samplePosts, // where mockPostsData is your mocked posts data
        });
  
        const latNearToPostOne = 50;
        const lonNearToPostOne = 50;

        // Call your endpoint and assert the response
        const response = await request(app).get('/tags/nearby').query({ latitude: latNearToPostOne, longitude: lonNearToPostOne});
        expect(response.status).toBe(200);
        expect(response.body).toEqual(expectedTags);
        
    });

    //ChatGPT Usage: Partial
    // Test Case 4: Missing Latitude or Longitude Parameters
    // Input: GET request without latitude or longitude parameters
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing parameters.
    // Expected Output: Error message "Missing user latitude or longitude
    it('should return 400 if latitude or longitude is missing', async () => {
        const response = await request(app).get('/tags/nearby');
        expect(response.status).toBe(400);
        expect(response.text).toBe("Missing user latitude or longitude");
    });

    //ChatGPT Usage: No
    // Test Case 5: Handling Errors and Returning a 500 Status Code
    // Input: GET request where an error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an error and logs it.
    // Expected Output: Error message and a 500 status code.
    it('should handle errors and return a 500 status code', async () => {
        // Mocking an error in your route by throwing an exception
          const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();
  
          mockFind.mockImplementationOnce(() => {
              throw new Error("Internal Server Error");
          });
          const response = await request(app).get('/tags');
          expect(response.status).toBe(500);
          expect(consoleErrorMock).toHaveBeenCalledWith('Internal Server Error');
      });
});