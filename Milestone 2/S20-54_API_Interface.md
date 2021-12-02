<style>
get {
	color: #fff;
    font-weight: 500;
    padding: 1px 15px 1px 15px;
    border-radius: 2px;
    background: #61AFFE;
}

get:before {
	content: "GET"
}

post {
	color: #fff;
    font-weight: 500;
    padding: 1px 10px 1px 10px;
    border-radius: 2px;
    background: #49CC90;
}

post:before {
	content: "POST"
}

put {
	color: #fff;
    font-weight: 500;
    padding: 1px 15px 1px 15px;
    border-radius: 2px;
    background: #FCA130;
}

put:before {
	content: "PUT"
}

delete {
	color: #fff;
    font-weight: 500;
    padding: 1px 5px 1px 5px;
    border-radius: 2px;
    background: #F93E3E;
}

delete:before {
	content: "DELETE"
}
</style>

                 
| Method            | Path                                         | Definition                      |
|:-----------------:|:-------------------------------------------- | :------------------------------ |
| <post></post>     | auth/signup                                  | registers user                  |
| <post></post>     | auth/signin                                  | sign in user                    |
|-------------------|----------------------------------------------|---------------------------------|
| <get></get>       | users                                        | gets list of users              |
| <put></put>       | users/:id                                    | updates user info               |
| <put></put>       | users/:id/password                           | updates user password           |
| <get></get>       | users/:id                                    | gets specific user              |
| <put></put>       | users/:id/follow                             | follows/unfollows user          |
| <get></get>       | users/:id/followers                          | gets list of user followers     |
| <get></get>       | users/:id/following                          | gets list of users followed     |
| <get></get>       | users/:id/collections                        | gets list of user collections   |
| <get></get>       | users/:id/liked                              | gets list of liked items        |
|-------------------|----------------------------------------------|---------------------------------|
| <get></get>       | users/:id/history                            | gets tracks history of user     |
| <post></post>     | users/:id/history                            | adds to user history            |
|-------------------|----------------------------------------------|---------------------------------|
| <get></get>       | users/:id/queue                              | gets user queue                 |
| <post></post>     | users/:id/queue                              | adds to user queue              |
| <delete></delete> | users/:id/queue                              | removes from user queue         |
|-------------------|----------------------------------------------|---------------------------------|
| <get></get>       | collections/:id                              | gets specific collection        |
| <put></put>       | collections/:id                              | updates collection info         |
| <post></post>     | collections/:id                              | creates collection              |
| <post></post>     | collections/:id/tracks                       | adds track to collection        |
| <delete></delete> | collections/:id                              | removes collection              |
| <delete></delete> | collections/:collection_id/tracks/:track_id  | removes track from collection   |
|-------------------|----------------------------------------------|---------------------------------|
| <get></get>       | tracks/:id                                   | returns track info              |
| <get></get>       | tracks/:id/stream                            | returns audio stream            |
| <put></put>       | tracks/:id                                   | updates track info              |
| <post></post>     | tracks/:id                                   | adds tracks                     |
| <delete></delete> | tracks/:id                                   | removes track                   |