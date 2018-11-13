// packages
const express = require('express'),
    router = express.Router(),
    error = require('./misc/error'),
    host = process.env.HOST;

// add routes
router.get('/', function (req, res) {
    res.json({
        title: 'Scrumble API',
        //documentation: 'https://drive.google.com/open?id=1Q7M9immTpeOPTIU5YpzBSn9f_k-VgX0Ii9DalNl0xmg',
        //auth: host + '/auth',
        //user: host + '/user',
        //catalog: host + '/catalog'
    });
});

router.post('/login', (req, res) => {
    let query = "select * from sc_user where username = :username and password = :password",
        param = [req.params.username, req.params.password];

    oracleConnection.execute(query, param)
        .then((result) => res.json(classParser(result.rows[0], classes.User)))
        .catch((err) => error.respondWith(res, err));
});

router.post('/register', (req, res) => {
    let query = "insert into sc_user values(null, lower(:username), :password)",
        param = [req.params.username, req.params.password];

    oracleConnection.execute(query, param)
        .then((result) => {
            query = "select max(id) from sc_user order by id",
                param = [];

            oracleConnection.execute(query, param)
                .then((result) => res.json({id: result.rows[0][0]}))
                .catch((err) => error.respondWith(res, err));
        })
        .catch((err) => error.respondWith(res, err));
});
module.exports = router;