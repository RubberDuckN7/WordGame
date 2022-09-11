precision mediump float;

uniform vec3 u_LightPos;
uniform sampler2D u_Texture;
varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;

void main()
{
    float distance = length(u_LightPos - v_Position);
    vec3 lightVector = normalize(u_LightPos - v_Position);

    float diffuse = max(dot(v_Normal, lightVector), 0.0);

    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance)));
    diffuse = diffuse + 0.7;

    gl_FragColor = (texture2D(u_Texture, v_TexCoordinate));
    if(gl_FragColor.a < 0.5)
       discard;
    gl_FragColor.rgb *= vec3(1.0, 1.0, 1.0);
}
