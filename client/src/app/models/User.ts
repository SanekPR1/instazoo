export interface User {
    id?: number;
    email: string;
    username: string;
    firstname: string;
    lastname: string;
    bio: string;
    password?: string;
    confirmedPassword?: string;
}

export interface LoginForm {
    username: string;
    password: string;
}