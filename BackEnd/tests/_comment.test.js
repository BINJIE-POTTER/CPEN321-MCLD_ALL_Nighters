const request = require('supertest');
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
    v4: jest.fn().mockReturnValue('unique_comment_id')
}));

const app = require('../server');

//Interface: www.mydomain.ca/comments
describe('POST /comments', () => {  

    //ChatGPT Usage: Partial 
    // Test Case 1: Successful Comment Insertion
    // Input: POST request with valid comment data
    // Expected Status Code: 200
    // Expected Behavior: The server successfully inserts the comment into the database.
    // Expected Output: Success message "Item received successfully".
    it('should successfully insert a comment', async () => {
      const comment = {
        userId: 'user1',
        pid: 'post1',
        content: 'This is a test comment',
        time: '2023-11-22-3:25'
      };
      const response = await request(app).post('/comments').send(comment);
      expect(response.status).toBe(200);
      expect(response.text).toBe("Item received successfully");
      expect(uuid.v4).toHaveBeenCalled();
      expect(mockInsertOne).toHaveBeenCalledWith({ ...comment, cid: 'unique_comment_id' });
    });
  
    //ChatGPT Usage: No
    // Test Case 2: Invalid Comment Format
    // Input: POST request with missing or invalid comment fields
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to invalid comment format.
    // Expected Output: Error message "Invalid comment format".
    it('should return 400 for invalid comment format', async () => {
      const invalidComment = {
      };
      const response = await request(app).post('/comments').send(invalidComment);
      expect(response.status).toBe(400);
      expect(response.text).toBe("Invalid comment format");
    });
  
    //ChatGPT Usage: No 
    // Test Case 3: Handling Database Errors
    // Input: POST request with valid comment data but the database operation fails
    // Expected Status Code: 500
    // Expected Behavior: The server encounters a database error and logs it.
    // Expected Output: Error message indicating server error.
    it('should handle database errors', async () => {
        const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();

        const comment = {
            userId: 'user1',
            pid: 'post1',
            content: 'This is a test comment',
            time: '2023-11-22-3:25'
          };

        mockInsertOne.mockImplementationOnce(() => {
            throw new Error("Internal Server Error");
        });
        const response = await request(app).post('/comments').send(comment);
        expect(response.status).toBe(500);
        expect(consoleErrorMock).toHaveBeenCalledWith('Internal Server Error');
    });
});

//Interface: www.mydomain.ca/comments
describe('GET /comments', () => {
    const sampleComments = [
        { pid: 'post1', text: 'Comment 1' },
        { pid: 'post1', text: 'Comment 2' },
        { pid: 'post2', text: 'Comment on another post' },
    ];

    beforeEach(() => {
        mockFind.mockClear();
        mockFind.mockReturnValue({
            toArray: () => sampleComments,
        });
   
    });

    //ChatGPT Usage: Partial
    // Test Case 4: Retrieve Comments for a Given Post ID
    // Input: GET request with a valid post_id (pid)
    // Expected Status Code: 200
    // Expected Behavior: The server retrieves comments associated with the given post_id.
    // Expected Output: Array of comments related to the specified post_id.
    it('should return comments for a given post_id', async () => {
        const response = await request(app).get('/comments').query({ pid: 'post1' });
        expect(response.status).toBe(200);
        expect(response.body).toEqual([
            { pid: 'post1', text: 'Comment 1' },
            { pid: 'post1', text: 'Comment 2' },
        ]);
    });

    //ChatGPT Usage: Partial
    // Test Case 5: Missing Post ID Parameter
    // Input: GET request without a post_id (pid)
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing post_id.
    // Expected Output: Error message "Missing post_id (pid)".
    it('should return 400 if post_id (pid) is missing', async () => {
        const response = await request(app).get('/comments');
        expect(response.status).toBe(400);
        expect(response.text).toBe("Missing post_id (pid)");
    });

    //ChatGPT Usage: No
    // Test Case 6: Handling Database Errors
    // Input: GET request where a database error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters a database error and logs it.
    // Expected Output: Error message indicating server error.
    it('should handle database errors', async () => {
        const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();

        mockFind.mockImplementationOnce(() => {
            throw new Error("Internal Server Error");
        });

        const response = await request(app).get('/comments').query({ pid: 'post1' });
        expect(response.status).toBe(500);
        expect(consoleErrorMock).toHaveBeenCalledWith('Internal Server Error');
    });
});

//Interface: www.mydomain.ca/comments
describe('DELETE /comments', () => {

    //ChatGPT Usage: Partial
    // Test Case 7: Missing Required Parameters
    // Input: DELETE request without necessary parameters (cid, pid, userId)
    // Expected Status Code: 400
    // Expected Behavior: The server rejects the request due to missing parameters.
    // Expected Output: Error message "Missing required parameters: cid, pid, or userId".
    it('should return 400 if any required parameter is missing', async () => {
        const response = await request(app).delete('/comments');
        expect(response.status).toBe(400);
        expect(response.text).toBe("Missing required parameters: cid, pid, or userId");
    });

    //ChatGPT Usage: Partial 
    // Test Case 8: Comment Does Not Exist
    // Input: DELETE request for a comment that does not exist in the database
    // Expected Status Code: 404
    // Expected Behavior: The server returns an error when the specified comment is not found.
    // Expected Output: Error message "Comment not found".
    it('should return 404 if the comment does not exist', async () => {
        mockFindOne.mockResolvedValue(null);
        const response = await request(app).delete('/comments').query({ cid: 'cid1', pid: 'pid1', userId: 'user1' });
        expect(response.status).toBe(404);
        expect(response.text).toBe("Comment not found");
    });

    //ChatGPT Usage: Partial 
    // Test Case 9: User Not Authorized to Delete the Comment
    // Input: DELETE request by a user who does not own the comment
    // Expected Status Code: 403
    // Expected Behavior: The server rejects the request due to lack of user authorization.
    // Expected Output: Error message "User is not authorized to delete this comment".
    it('should return 403 if the user is not authorized to delete the comment', async () => {
        const comment = { cid: 'cid1', pid: 'pid1', userId: 'user2' };
        mockFindOne.mockResolvedValue(comment);
        const response = await request(app).delete('/comments').query({ cid: 'cid1', pid: 'pid1', userId: 'user1' });
        expect(response.status).toBe(403);
        expect(response.text).toBe("User is not authorized to delete this comment");
    });

    //ChatGPT Usage: Partial
    // Test Case 10: Successful Comment Deletion
    // Input: DELETE request with valid parameters for a comment that exists and is owned by the user
    // Expected Status Code: 200
    // Expected Behavior: The server successfully deletes the comment.
    // Expected Output: Success message "Comment deleted successfully".
    it('should delete the comment successfully', async () => {
        const comment = { cid: 'cid1', pid: 'pid1', userId: 'user1' };
        mockFindOne.mockResolvedValue(comment);
        mockDeleteOne.mockResolvedValue({ deletedCount: 1 });
        const response = await request(app).delete('/comments').query({ cid: 'cid1', pid: 'pid1', userId: 'user1' });
        expect(response.status).toBe(200);
        expect(response.text).toBe("Comment deleted successfully");
    });

    //ChatGPT Usage: No
    // Test Case 11: Handling Internal Server Error
    // Input: DELETE request where an internal server error occurs
    // Expected Status Code: 500
    // Expected Behavior: The server encounters an internal error and logs it.
    // Expected Output: Error message indicating server error.
    it('should handle internal server error', async () => {
        const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();
        const comment = { cid: 'cid1', pid: 'pid1', userId: 'user1' };
        mockFindOne.mockResolvedValue(comment);
        mockDeleteOne.mockImplementationOnce(() => {
            throw new Error("Internal Server Error");
        });
        const response = await request(app).delete('/comments').query({ cid: 'cid1', pid: 'pid1', userId: 'user1' });
        expect(response.status).toBe(500);
        expect(consoleErrorMock).toHaveBeenCalledWith('Internal Server Error');
    });

    //ChatGPT Usage: Partial
    // Test Case 12: Handling Database Error
    // Input: DELETE request where a database error occurs (e.g., the comment does not get deleted)
    // Expected Status Code: 500
    // Expected Behavior: The server encounters a database error and logs it.
    // Expected Output: Error message indicating server error.
    it('should handle database error', async () => {
        const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();
        const comment = { cid: 'cid1', pid: 'pid1', userId: 'user1' };
        mockFindOne.mockResolvedValue(comment);
        mockDeleteOne.mockResolvedValue({ deletedCount: 0 });
        const response = await request(app).delete('/comments').query({ cid: 'cid1', pid: 'pid1', userId: 'user1' });
        expect(response.status).toBe(500);
        expect(consoleErrorMock).toHaveBeenCalledWith('Internal Server Error');
    });

});