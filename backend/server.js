const express = require('express');
const connectDB = require('./db.js');
const { PORT } = require('./constants.js');
const app = express();
const flightRouter = require('./controllers/flightController.js');
const groupRouter = require('./controllers/groupController.js');

// Middleware
app.use(express.json());
app.use('/flights', flightRouter);
app.use('/groups', groupRouter);

// Routes
// app.use('/api/users', userRoutes);

// Simple GET endpoint
app.get('/', (req, res) => {
    res.send('Hello, World!');
});

const startServer = async () => {
    await connectDB();
    app.listen(PORT, () => {
        console.log(`Server running on port ${PORT}`);
    });
};

startServer();
