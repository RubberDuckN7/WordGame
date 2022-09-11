uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TexCoordinate;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;

void main()
{
    v_Position = vec3(u_MVMatrix * a_Position);

    v_TexCoordinate = a_TexCoordinate;
    // Transform the normal's orientation into eye space.\n" +
    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));
    // gl_Position is a special variable used to store the final position.\n" +
    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.\n" +
    gl_Position = u_MVPMatrix * a_Position;
}

