module.exports = {
    // Other Jest configuration options...
  
    // Indicates whether the coverage information should be collected
    // while executing the test
    collectCoverage: true,
  
    // An array of glob patterns indicating a set of files for which
    // coverage information should be collected
    collectCoverageFrom: [
      '**/_commentRoutes.js', // Include _commentRoutes.js
      '**/_postRoutes.js',    // Include _postRoutes.js
      '**/_tagRoutes.js',     // Include _tagRoutes.js
      '**/_userRoutes.js',    // Include _userRoutes.js
    ],
      
    // The directory where Jest should output its coverage files
    coverageDirectory: 'coverage',
  
    // An array of regexp pattern strings that are matched against
    // all file paths before executing the test to skip coverage
    coveragePathIgnorePatterns: ['/node_modules/'],
  
    // Reporters that Jest uses when writing coverage reports
    coverageReporters: ['clover', 'json', 'lcov', 'text'],
  
    // Minimum threshold enforcement for coverage results
    coverageThreshold: {
      global: {
        branches: 80,
        functions: 80,
        lines: 80,
        statements: 80,
      },
      // You can add more specific thresholds if needed
    },
  
    // Ensure that Jest runs in CI environment (e.g., GitHub Actions)
    // This will help to handle timers and other configurations for CI
    testEnvironment: 'node',
  };