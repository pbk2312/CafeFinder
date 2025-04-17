CREATE TABLE cafe (
    code VARCHAR(10) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    district VARCHAR(10) NOT NULL,
    opening_hours VARCHAR(20),
    phone_number VARCHAR(30),
    image_url TEXT,
    average_rating DOUBLE PRECISION
);

CREATE TABLE cafe_themes (
    cafe_code VARCHAR(10) NOT NULL,
    theme VARCHAR(30) NOT NULL,
    PRIMARY KEY (cafe_code, theme),
    FOREIGN KEY (cafe_code) REFERENCES cafe(code)
);

CREATE TABLE cafe_review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cafe_code VARCHAR(10) NOT NULL,
    rating DOUBLE NOT NULL,
    content LONGTEXT,
    FOREIGN KEY (cafe_code) REFERENCES cafe(code)
);

CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nick_name VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    provider VARCHAR(20),
    member_role VARCHAR(20),
    profile_image_path VARCHAR(255)
);

CREATE TABLE seoul_district_status (
                                       gu_code VARCHAR(10) NOT NULL PRIMARY KEY,
                                       average_rating DOUBLE PRECISION NOT NULL,
                                       total_reviews INT NOT NULL
);

CREATE TABLE cafe_scrap (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    cafe_code VARCHAR(10) NOT NULL,
    CONSTRAINT fk_member
        FOREIGN KEY (member_id)
        REFERENCES member(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_cafe
        FOREIGN KEY (cafe_code)
        REFERENCES cafe(code)
        ON DELETE CASCADE
);








