
INSERT INTO permissions (name)
VALUES
    ("Search User"),
    ("View User Details"),
    ("Create New User"),
    ("Delete User"),
    ("View Roles"),
    ("View Permissions"),
    ("Update Permissions");

INSERT INTO role_permissions (role, permission_id)
VALUES
    (1,1), (1,2), (1,3), (1,4), (1,5), (1,6), (1,7),
    (2,1), (2,2), (2,3), (2,4), (2,6), (2,7),
    (3,1), (3,2), (3,6), (3,7);

INSERT INTO users (username, password, email, role)
VALUES
    ('Manil Tenuka','$2a$10$6HzztvIj8kff34EkGU7DbuFU4fcY5srm026lLdiUt3fe4XUppD91G','maniltenuka@gmail.com', "Owner");
